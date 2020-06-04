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
package europass.ewa.services.social;

import java.util.List;

import europass.ewa.model.Achievement;
import europass.ewa.model.CodeLabel;
import europass.ewa.services.social.InstanceClassMismatchException;
import europass.ewa.services.social.Transformer;
import europass.ewa.services.social.MockLinkedInProfile.Course;
import europass.ewa.services.social.MockLinkedInProfile.CustomSkill;

public class MockHandlers {
	
	static class InstantMessagingTypeHandler implements Transformer{

		@SuppressWarnings ( "unchecked")
		@Override
		public Object transform( Object from, Object to, Object... params ) throws InstanceClassMismatchException {
			if ( !(from instanceof String) ){ throw new InstanceClassMismatchException(); }
			if ( !(to instanceof CodeLabel) ){ throw new InstanceClassMismatchException(); }
			
			String imType = (String) from;
			CodeLabel use = (CodeLabel) to;
			
			use.setCode( imType );
			//WE normally need to use the Taxonomy bundle for this, but OK
			use.setLabel( imType + "_LABEL" );
			return use;
		}
		
	}
	static class SimpleHandler implements Transformer{

		@SuppressWarnings ( "unchecked")
		@Override
		public Object transform( Object from, Object to, Object... params ) throws InstanceClassMismatchException  {
			if ( !(from instanceof String) ){ throw new InstanceClassMismatchException(); }
			if ( !(to instanceof String) ){ throw new InstanceClassMismatchException(); }
			
			to = from + "_" + (String) params[0];
			return to;
		}
	}
	
	/**
	 * This would be used when the LinkedIn Skills is a List of structured objects
	 * @author ekar
	 *
	 */
	static class SkillsHandler implements Transformer{

		@SuppressWarnings ( "unchecked")
		@Override
		public Object transform( Object from, Object to, Object... params ) throws InstanceClassMismatchException  {
			if ( !(from instanceof List<?>) ){ throw new InstanceClassMismatchException(); }
			if ( !(to instanceof String) ){ throw new InstanceClassMismatchException(); }
			
			List<CustomSkill> list = (List<CustomSkill>) from;
			if ( list.size() == 0 ){ 
				return to; 
			}
			
			StringBuilder bld = new StringBuilder( "" );
			for ( int i = 0; i< list.size(); i++ ){
				bld.append(  i>0 ? ", " : "" );
				CustomSkill skill = list.get( i );
				bld.append( skill.getName() );
			}
			to = bld.toString();
			return to;
		}
	}
	
	static class CoursesHandler implements Transformer{
		@SuppressWarnings ( "unchecked")
		@Override
		public Object transform( Object from, Object to, Object... params ) throws InstanceClassMismatchException  {
			
			if ( !(from instanceof List<?>) ){ throw new InstanceClassMismatchException(); }
			if ( !(to instanceof List<?>) ){ throw new InstanceClassMismatchException(); }
			
			try{
				
				List<Achievement> listTo = (List<Achievement>) to;
				CodeLabel title = new CodeLabel("courses", "Courses Attended");
				Achievement courses = new Achievement();
				courses.setTitle( title );
				
				StringBuilder bld = new StringBuilder();
				
				List<Course> listFrom = (List<Course>) from;
				int size = listFrom.size();
				
				for ( int i = 0; i <size; i++ ){
					bld.append( (i == 0 ) ? "<ul>" : "" );
					Course item = listFrom.get( i );
					bld.append("<li>" + item.getName() +"</li>");
					bld.append( i == (size - 1)  ? "</ul>" : "" );
				}
				courses.setDescription( bld.toString() );
				
				listTo.add( courses );
				return listTo;
			} catch ( final Exception e ){
				return to;
			}
		}
		
	}
}
