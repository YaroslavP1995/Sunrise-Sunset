package com.example.slavik.sunrise_sunset;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Slavik on 10.06.2018.
 */

public interface SunriseSunsetApi {

     @GET("/json")
     Call<Results> getData(@Query("lat") double lat, @Query("lng") double lng,@Query("formatted") int formatted);






















































































































    //в дженерыку вказав клас
     //getData, возвращающий объект типа Call<List<PostModel>>. Методы должны всегда возвращать объект типа Call<T>
     //Аннотация @Query("name") String resourceName показывает Retrofit'у, что в качестве параметра запроса нужно поставить
     // пару name=<Значение строки resourceName>.
     //"lat=36.7201600&lng") float lat, @Query("-4.4203400&date") float lng,
     //("today") String date);      //в дженерыку вказав клас

}
