/* 
 * Copyright (c) 2002-2020 Cedefop.
 * 
 * This file is part of EWA (Cedefop).
 * 
 * EWA (Cedefop) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * EWA (Cedefop) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with EWA (Cedefop). If not, see <http ://www.gnu.org/licenses/>.
 */
package europass.ewa.model.format;

import static org.junit.Assert.assertThat;

import org.hamcrest.CoreMatchers;
import org.junit.BeforeClass;
import org.junit.Test;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

public class HtmlSanitizerTest {

    static PolicyFactory policy;

    @BeforeClass
    public static void prepare() {
        policy = new HtmlPolicyBuilder()
                .allowElements("a", "br", "p", "b", "i", "u", "ul", "ol", "li", "strong", "em")
                .allowUrlProtocols("http", "https", "mailto")
                .allowAttributes("href", "target").onElements("a")
                .requireRelNofollowOnLinks()
                .toFactory();
    }
    //No Html 

    @Test
    public void test0() {
        String html = "Apple > Orange but < Banana";
        String sane = policy.sanitize(html);
        assertThat(sane, CoreMatchers.is("Apple &gt; Orange but &lt; Banana"));
    }
    //No Filter Evasion 

    @Test
    public void test1() {
        String html = "<SCRIPT SRC=http://ha.ckers.org/xss.js></SCRIPT>";
        String sane = policy.sanitize(html);
        assertThat(sane, CoreMatchers.is(""));
    }
    //Image XSS using the JavaScript directive 

    @Test
    public void test2() {
        String html = "<IMG SRC=\"javascript:alert('XSS');\">";
        String sane = policy.sanitize(html);
        assertThat(sane, CoreMatchers.is(""));
    }
    //No quotes and no semicolon 

    @Test
    public void test3() {
        String html = "<IMG SRC=javascript:alert('XSS')>";
        String sane = policy.sanitize(html);
        assertThat(sane, CoreMatchers.is(""));
    }
    //Case insensitive XSS attack vector

    @Test
    public void test4() {
        String html = "<IMG SRC=JaVaScRiPt:alert('XSS')>";
        String sane = policy.sanitize(html);
        assertThat(sane, CoreMatchers.is(""));
    }
    //HTML entities 

    @Test
    public void test5() {
        String html = "<IMG SRC=javascript:alert(\"XSS\")>";
        String sane = policy.sanitize(html);
        assertThat(sane, CoreMatchers.is(""));
    }
    //Grave accent obfuscation  

    @Test
    public void test6() {
        String html = "<IMG SRC=`javascript:alert(\"RSnake says, 'XSS'\")`>";
        String sane = policy.sanitize(html);
        assertThat(sane, CoreMatchers.is(""));
    }
    //Malformed A tags 

    @Test
    public void test7() {
        String html = "<a onmouseover=\"alert(document.cookie)\">xxs link</a>";
        String sane = policy.sanitize(html);
        assertThat(sane, CoreMatchers.is("xxs link"));
    }
    //Malformed IMG tags

    @Test
    public void test8() {
        String html = "<IMG \"\"\"><SCRIPT>alert(\"XSS\")</SCRIPT>\">";
        String sane = policy.sanitize(html);
        assertThat(sane, CoreMatchers.is("&#34;&gt;"));
    }
    //fromCharCode  

    @Test
    public void test9() {
        String html = "<IMG SRC=javascript:alert(String.fromCharCode(88,83,83))>";
        String sane = policy.sanitize(html);
        assertThat(sane, CoreMatchers.is(""));
    }
    //Default SRC tag to get past filters that check SRC domain 

    @Test
    public void test10() {
        String html = "<IMG SRC=# onmouseover=\"alert('xxs')\">";
        String sane = policy.sanitize(html);
        assertThat(sane, CoreMatchers.is(""));
    }
    // Default SRC tag by leaving it empty 

    @Test
    public void test11() {
        String html = "<IMG SRC= onmouseover=\"alert('xxs')\">";
        String sane = policy.sanitize(html);
        assertThat(sane, CoreMatchers.is(""));
    }
    // Default SRC tag by leaving it out entirely 

    @Test
    public void test12() {
        String html = "<IMG onmouseover=\"alert('xxs')\">";
        String sane = policy.sanitize(html);
        assertThat(sane, CoreMatchers.is(""));
    }
    // UTF-8 Unicode encoding 

    @Test
    public void test13() {
        String html = "<IMG SRC=&#106;&#97;&#118;&#97;&#115;&#99;&#114;&#105;&#112;&#116;&#58;&#97;&#108;&#101;&#114;&#116;&#40;&#39;&#88;&#83;&#83;&#39;&#41;>";
        String sane = policy.sanitize(html);
        assertThat(sane, CoreMatchers.is(""));
    }
    //Long UTF-8 Unicode encoding without semicolons 

    @Test
    public void test14() {
        String html = "<IMG SRC=&#0000106&#0000097&#0000118&#0000097&#0000115&#0000099&#0000114&#0000105&#0000112&#0000116&#0000058&#0000097&#0000108&#0000101&#0000114&#0000116&#0000040&#0000039&#0000088&#0000083&#0000083&#0000039&#0000041>";
        String sane = policy.sanitize(html);
        assertThat(sane, CoreMatchers.is(""));
    }
    //Hex encoding without semicolons 

    @Test
    public void test15() {
        String html = "<IMG SRC=&#x6A&#x61&#x76&#x61&#x73&#x63&#x72&#x69&#x70&#x74&#x3A&#x61&#x6C&#x65&#x72&#x74&#x28&#x27&#x58&#x53&#x53&#x27&#x29>";
        String sane = policy.sanitize(html);
        assertThat(sane, CoreMatchers.is(""));
    }
    //Embedded tab

    @Test
    public void test16() {
        String html = "<IMG SRC=\"jav	ascript:alert('XSS');\">";
        String sane = policy.sanitize(html);
        assertThat(sane, CoreMatchers.is(""));
    }
    //Embedded Encoded tab

    @Test
    public void test17() {
        String html = "<IMG SRC=\"jav&#x09;ascript:alert('XSS');\">";
        String sane = policy.sanitize(html);
        assertThat(sane, CoreMatchers.is(""));
    }
    //Embedded newline to break up XSS

    @Test
    public void test18() {
        String html = "<IMG SRC=\"jav&#x0A;ascript:alert('XSS');\">";
        String sane = policy.sanitize(html);
        assertThat(sane, CoreMatchers.is(""));
    }
}
