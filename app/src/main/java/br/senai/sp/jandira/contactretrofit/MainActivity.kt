package br.senai.sp.jandira.contactretrofit

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.senai.sp.jandira.contactretrofit.API.ContactCall
import br.senai.sp.jandira.contactretrofit.API.RetrofitApi
import br.senai.sp.jandira.contactretrofit.model.Contact
import br.senai.sp.jandira.contactretrofit.ui.theme.ContactRetrofitTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ContactRetrofitTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting()
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Greeting() {

    val context = LocalContext.current

    var nameState by remember {
        mutableStateOf("")
    }
    var emailState by remember {
        mutableStateOf("")
    }
    var phoneState by remember {
        mutableStateOf("")
    }
    var activeState by remember {
        mutableStateOf(false)
    }
    var contacts by remember {
        mutableStateOf(listOf<Contact>())
    }

    val retrofit = RetrofitApi.getRetrofit() // pegar a instância do retrofit
    val contactsCall = retrofit.create(ContactCall::class.java) // instância do objeto contact
    val callGetContacts = contactsCall.getAll()

    // Excutar a chamada para o End-point
    callGetContacts.enqueue(object: Callback<List<Contact>>{ // enqueue: usado somente quando o objeto retorna um valor
            override fun onResponse(call: Call<List<Contact>>, response: Response<List<Contact>>) {
                contacts = response.body()!!
            }

            override fun onFailure(call: Call<List<Contact>>, t: Throwable) {
                Log.i("ds3m", t.message.toString())
            }
        }
    )

    Column() {

        Text(text = "Cadastro de contatos")

        OutlinedTextField(
            value = nameState,
            onValueChange = {
                nameState = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            label = {
                Text(text = "Contact Name")
            }
        )
        OutlinedTextField(
            value = emailState,
            onValueChange = {
                emailState = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = "Contact E-mail")
            }
        )
        OutlinedTextField(
            value = phoneState,
            onValueChange = {
                phoneState = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = "Contact Phone")
            }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = activeState,
                onCheckedChange = {
                    activeState = !activeState
                }
            )

            Text(text = "Enable")
            
        }
        Button(
            onClick = {
                // objeto com os dados dos inputs
                val contact = Contact(
                    name = nameState,
                    email = emailState,
                    phone = phoneState,
                    active = activeState
                )

                val contactPost = contactsCall.saveContact(contact)

                contactPost.enqueue(object: Callback<Contact>{
                    override fun onResponse(call: Call<Contact>, response: Response<Contact>) {
                        Log.i("ds3m", response.body()!!.toString())
                    }

                    override fun onFailure(call: Call<Contact>, t: Throwable) {
                        Log.i("ds3m", t.message.toString())
                    }
                })
            }
        ) {
            Text(text = "save new contact")
        }
        LazyColumn(modifier = Modifier.fillMaxWidth()){
            items(contacts){
                var visibilityButton by remember {
                    mutableStateOf(false)
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable {
                            nameState = it.name
                            emailState = it.email
                            phoneState = it.phone
                            activeState = it.active

                            visibilityButton = !visibilityButton
                        },
                    backgroundColor = Color(123, 168, 161, 255)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp),
                    ) {
                        Text(text = it.name)
                        Text(text = it.email)
                        Text(text = it.phone)
                        if (visibilityButton) Button(
                            onClick = {
                                val contactDelete = contactsCall.deleteContact(it.id)

                                contactDelete.enqueue(object: Callback<String>{
                                    override fun onResponse(call: Call<String>, response: Response<String>) {
                                        Toast.makeText(context, response.code().toString(), Toast.LENGTH_LONG).show()
                                    }

                                    override fun onFailure(call: Call<String>, t: Throwable) {
                                    }
                                })
                            }) {
                            Text(text = "Delete")
                        }
                    }
                }
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ContactRetrofitTheme {
        Greeting()
    }
}