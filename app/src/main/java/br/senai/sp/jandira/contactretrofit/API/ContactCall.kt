package br.senai.sp.jandira.contactretrofit.API

import br.senai.sp.jandira.contactretrofit.model.Contact
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// interface para chamada da API
interface ContactCall {

    @GET("contacts")
    fun getAll(): Call<List<Contact>>

    @POST("contacts")
    fun saveContact(@Body contact: Contact): Call<Contact>

    @DELETE("contacts/{id}")
    fun deleteContact(@Path("id") id: Long): Call<String>
}