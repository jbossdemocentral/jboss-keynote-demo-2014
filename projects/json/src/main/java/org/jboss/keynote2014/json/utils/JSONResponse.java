package org.jboss.keynote2014.json.utils;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class JSONResponse
{
    private final int statusCode;
    private final JSONObject jsonObject;
    private final JSONArray jsonArray;

    public JSONResponse(final int statusCode)
    {
        this(statusCode, null, null);
    }

    public JSONResponse(final int statusCode, final JSONObject jsonObject)
    {
        this(statusCode, jsonObject, null);
    }

    public JSONResponse(final int statusCode, final JSONArray jsonArray)
    {
        this(statusCode, null, jsonArray);
    }
    
    private JSONResponse(final int statusCode, final JSONObject jsonObject, final JSONArray jsonArray)
    {
        this.statusCode = statusCode;
        this.jsonObject = jsonObject;
        this.jsonArray = jsonArray;
    }

    public int getStatusCode()
    {
        return statusCode;
    }

    public JSONObject getPayloadAsJSONObject()
    {
        return jsonObject;
    }

    public JSONArray getPayloadAsJSONArray()
    {
        return jsonArray;
    }
}
