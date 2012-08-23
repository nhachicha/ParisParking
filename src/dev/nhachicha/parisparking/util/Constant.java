package dev.nhachicha.parisparking.util;
/*
Copyright 2012 Nabil HACHICHA

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

/**
 * @author Nabil HACHICHA
 * http://nhachicha.wordpress.com
 */
public class Constant {
    
    public final static String MANGODB_URL = "https://api.mongolab.com/api/1/databases";
    public final static String MANGODB_API_KEY = "?";
    public final static String MANGODB_DB_NAME = "?";
    public final static String MANGODB_COLLECTION_NAME = "?";
    
    //First param: Longitude, Second param: Latitude, Third param: Number of result
    public final static String JSON_SEARCH_PAYLOAD_TEMPLATE = "{\"geoNear\": \"places\",\"near\":[%f,%f], \"spherical\": true, \"distanceMultiplier\": 6378000, \"num\": %d}";
    public final static String JSON_SEARCH_URL = MANGODB_URL+"/"+MANGODB_DB_NAME+"/runCommand?apiKey="+MANGODB_API_KEY;
    
    //Default 
    public static  int DEFAULT_SEARCH_NUMBER_OF_RESULT = 5;
    public final static  int SEARCH_NUMBER_MAX = 20;
    public static boolean DEVELOPER_MODE = false;
}
