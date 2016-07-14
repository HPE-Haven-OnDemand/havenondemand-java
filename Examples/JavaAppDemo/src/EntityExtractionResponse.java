import java.util.List;

public class EntityExtractionResponse
{
    public List<Entity> entities;

    public class EntityAdditionalInformation
    {
        public List<String> person_profession;
        public String person_date_of_birth;
        public Long wikidata_id;
        public String wikipedia_eng;
        public String image;
        public String person_date_of_death;
        public Double lon;
        public Double lat;
        public Long place_population;
        public String place_country_code;
        public String place_region1;
        public String place_region2;
        public String url_homepage;
        public Double place_elevation; 
        public String place_type;
        public String place_continent;
    }
	public class Components
	{   
		public Integer original_length;  
		public String original_text;  
		public String type;
	}
    public class Entity
    {
        public String normalized_text;
        public String original_text;
        public String type;
        public Long normalized_length;
        public Long original_length;
        public Double score;
        public String normalized_date;
        public EntityAdditionalInformation additional_information;
        public List<Components> components;
        public int documentIndex;
    }
}
