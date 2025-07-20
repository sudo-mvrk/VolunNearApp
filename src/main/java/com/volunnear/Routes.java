package com.volunnear;

public class Routes {

    private static final String API_URL = "/api/v1";

    /**
     * Users registration and login
     */
    private static final String AUTH = API_URL + "/auth";
    public static final String LOGIN = AUTH + "/login";
    public static final String REFRESH_TOKEN = AUTH + "/refresh-token";
    public static final String REGISTER = AUTH + "/register";

    public static final String REGISTER_VOLUNTEER = REGISTER + "/volunteer";
    public static final String REGISTER_ORGANIZATION = REGISTER + "/organization";

    /**
     *  User profiles
     */
    private static final String PROFILE = "/profile";

    public static final String VOLUNTEER_PROFILE = API_URL + "/volunteer" + PROFILE;
    public static final String ORGANIZATION_PROFILE = API_URL + "/organization" + PROFILE;
    /**
     * Activity routes
     */
    private static final String ORGANIZATIONS = API_URL + "/organizations";
    public static final String ACTIVITIES = API_URL + "/activities";
    public static final String ACTIVITY_BY_ID = ACTIVITIES + "/{id}";
    public static final String ORGANIZATION_BY_ID = ORGANIZATIONS + "/{id}";
    public static final String ORGANIZATION_ACTIVITIES_BY_ID = ORGANIZATIONS + "/{id}" + "/activities";
    public static final String ORGANIZATION_ACTIVITIES_BY_PRINCIPAL = ORGANIZATION_PROFILE + "/activities";

    public static final String VOLUNTEER_REQUESTS = API_URL + "/volunteer/requests";
    public static final String VOLUNTEER_ACTIVITY_REQUEST = ACTIVITY_BY_ID + "/request";



    public static final String[] SWAGGER_ENDPOINTS = {
            "api/v1/auth/**",
            "/v3/api-docs/**",
            "/v3/api-docs.yaml",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };
}
