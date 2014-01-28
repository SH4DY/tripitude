package ac.tuwien.ase08.tripitude.beans;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;


public class HibernateAwareObjectMapper extends ObjectMapper {
  
    /**
     * Register module to support lazy loaded items
     */
	public HibernateAwareObjectMapper() {
        registerModule(new Hibernate4Module());
        this.enableDefaultTypingAsProperty(DefaultTyping.JAVA_LANG_OBJECT, JsonTypeInfo.Id.CLASS.getDefaultPropertyName());
    }

}
