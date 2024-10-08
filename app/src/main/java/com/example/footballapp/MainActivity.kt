package com.example.footballapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.footballapp.ui.theme.FootballAppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import androidx.compose.foundation.Image
import coil.compose.rememberImagePainter

class MainActivity : ComponentActivity() {
    private lateinit var db: footballdata
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            //intalzise the database
            db = Room.databaseBuilder(
                applicationContext,
                footballdata::class.java, "football_leagues_database"
            )

                .build()

            FootballAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //displays the links function
                    links()
                }
            }
        }
    }

    //This is the main home area which has 3 button that take you to different areas of the app
    @Composable
    fun Mainpage(navController: NavHostController) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)

        ) {
            //When i click any of these buttons they will find the same "code" from MyApp
            //And take you to the function connected to it
            //sends me to add leagues function
            Button(
                onClick = {
                    navController.navigate("Add_Leagues_to_DB")
                },
                modifier = Modifier
                    .size(300.dp, 80.dp)
                    .padding(10.dp)
            ) {
                Text("Add Leagues To DB")
            }
            //sends me to the the search for clubs by league
            Button(
                onClick = { navController.navigate("Search_for_Clubs_By_League") },
                modifier = Modifier
                    .size(300.dp, 80.dp)
                    .padding(10.dp)
            ) {
                Text("Search For Clubs By League")
            }
            //sends me to the search for clubs function
            Button(
                onClick = { navController.navigate("Search_for_Clubs") },
                modifier = Modifier
                    .size(300.dp, 80.dp)
                    .padding(10.dp)
            ) {
                Text("Search For Clubs")
            }
        }

    }

    @Composable
    fun links() {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "main_home") {
            //When the back button is pressed this is called and sends you back to the main home page
            composable("main_home") {
                Mainpage(navController)
            }
            //When the Add League To DB is pressed this is called and sends you to the Function
            composable("Add_Leagues_to_DB") {
                addleaguetodb(navController)
            }
            //When the Search For Clubs By League is pressed this is called and sends you to the Function
            composable("Search_for_Clubs_By_League") {
                searchforclubsbyleague(navController)
            }
            //When the Search For Clubs is pressed this is called and sends you to the Function
            composable("Search_for_Clubs") {
                searchforclubs(navController)
            }
        }
    }


    @Composable
    fun addleaguetodb(navController: NavHostController) {

        //defines a list of leagues with their details which will be saved to the database
        val footballleagues = listOf(
            footballleague("4328", "English Premier League", "Soccer", "Premier League, EPL"),
            footballleague("4329", "English League Championship", "Soccer", "Championship"),
            footballleague("4330", "Scottish Premier League", "Soccer", "Scottish Premiership, SPFL"),
            footballleague("4331", "German Bundesliga", "Soccer", "Bundesliga, Fußball-Bundesliga"),
            footballleague("4332", "Italian Serie A", "Soccer", "Serie A"),
            footballleague("4334", "French Ligue 1", "Soccer", "Ligue 1 Conforama"),
            footballleague("4335", "Spanish La Liga", "Soccer", "LaLiga Santander, La Liga"),
            footballleague("4336", "Greek Superleague Greece", "Soccer", ""),
            footballleague("4337", "Dutch Eredivisie", "Soccer", "Eredivisie"),
            footballleague("4338", "Belgian First Division A", "Soccer", "Jupiler Pro League"),
            footballleague("4339", "Turkish Super Lig", "Soccer", "Super Lig"),
            footballleague("4340", "Danish Superliga", "Soccer", ""),
            footballleague("4344", "Portuguese Primeira Liga", "Soccer", "Liga NOS"),
            footballleague("4346", "American Major League Soccer", "Soccer", "MLS, Major League Soccer"),
            footballleague("4347", "Swedish Allsvenskan", "Soccer", "Fotbollsallsvenskan"),
            footballleague("4350", "Mexican Primera League", "Soccer", "Liga MX"),
            footballleague("4351", "Brazilian Serie A", "Soccer", ""),
            footballleague("4354", "Ukrainian Premier League", "Soccer", ""),
            footballleague("4355", "Russian Football Premier League", "Soccer", "Чемпионат России по футболу"),
            footballleague("4356", "Australian A-League", "Soccer", "A-League"),
            footballleague("4358", "Norwegian Eliteserien", "Soccer", "Eliteserien"),
            footballleague("4359", "Chinese Super League", "Soccer", "")
        )

        LaunchedEffect(Unit) {

            //runs a corotine that runs in the background when the button is pressed
            // the list is then inserted into the database
            withContext(Dispatchers.IO) {
                db.footballLeagueDao().insertleagues(footballleagues)
            }

            //navigates back to the previous page in this case itll just take you back to the homepage
            navController.popBackStack()
        }
    }

    @Composable
    fun searchforclubsbyleague(navController: NavHostController) {

        //variables to hold and remember from this composable
        var leaguename by remember { mutableStateOf("") }
        var clubs by remember { mutableStateOf<List<footballclubsbyleague>>(emptyList()) }
        val coroutinescope = rememberCoroutineScope()

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            //textfield for the user to enter league name so clubs can display from the league
            TextField(
                value = leaguename,
                onValueChange = { leaguename = it },
                label = { Text("Enter League Name") },
                modifier = Modifier.fillMaxWidth()
            )
            //when this button is pressed the clubs from that league will be shown
            Button(
                onClick = {
                    //using a coroutine it find clubd from that league
                    coroutinescope.launch {
                        searchclubs(leaguename) { clubsgrabbed ->
                            //updates the clubs list with fetched clubs
                            clubs = clubsgrabbed
                        }
                    }
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Retrieve Clubs")
            }

            //button to save the clubs that have been fetched into the database
            Button(
                onClick = {
                    //check if clubs are fetched
                    if (clubs.isNotEmpty()) {
                        coroutinescope.launch {
                            withContext(Dispatchers.IO) {
                                //insert the clubs into the database
                                db.ClubsDAO().insertclubs(clubs.map { club ->
                                    //details that will be saved
                                    Club(
                                        id = club.idTeam,
                                        name = club.Name,
                                        shortName = club.strTeamShort,
                                        alternate = club.strAlternate,
                                        formedYear = club.intFormedYear,
                                        league = club.strLeague,
                                        stadium = club.strStadium,
                                        keywords = club.strKeywords,
                                        stadiumThumb = club.strStadiumThumb,
                                        stadiumLocation = club.strStadiumLocation,
                                        stadiumCapacity = club.intStadiumCapacity,
                                        website = club.strWebsite,
                                        teamJersey = club.strTeamJersey,
                                        teamLogo = club.strTeamLogo
                                    )
                                })
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Clubs to Database")
            }

            //button to go back to the main page
            Button(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back to Main Page")
            }

            LazyColumn {
                items(clubs) { club ->
                    Column(
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {

                            }
                        //displays the club details when we press retrieve club button
                    ) {
                        Text(text = "Name: ${club.Name}", fontWeight = FontWeight.Bold)
                        Text(text = "Short Name: ${club.strTeamShort}")
                        Text(text = "Alternate Names: ${club.strAlternate}")
                        Text(text = "Formed Year: ${club.intFormedYear}")
                        Text(text = "League: ${club.strLeague}")
                        Text(text = "League ID: ${club.idLeague}")
                        Text(text = "Stadium: ${club.strStadium}")
                        Text(text = "Keywords: ${club.strKeywords}")
                        Text(text = "Stadium Location: ${club.strStadiumLocation}")
                        Text(text = "Stadium Capacity: ${club.intStadiumCapacity}")
                        Text(text = "Website: ${club.strWebsite}")
                        Text(text = "Team Jersey: ${club.strTeamJersey}")
                        Text(text = "Team Logo: ${club.strTeamLogo}")
                    }
                    Divider() //adds a separator between the clubs
                }
            }
        }
    }

    suspend fun searchclubs(leaguename: String, onComplete: (List<footballclubsbyleague>) -> Unit) {
        try {
            //encodes the league name so it can be used in the url
            val encodedleague = URLEncoder.encode(leaguename, "UTF-8")
            //creates a string for the url with the leaguename we provided in the textfield
            val stringofurl = "https://www.thesportsdb.com/api/v1/json/3/search_all_teams.php?l=$encodedleague"
            //logging for debugging
            Log.d("API", "URL: $stringofurl")

            //network task done in the background
            val response = withContext(Dispatchers.IO) {
                val url = URL(stringofurl)
                //makes an open connection with the url given
                val connection = url.openConnection() as HttpURLConnection
                //reads the data and then appends each line with string builder
                val response = StringBuilder()
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                //closes the reader and disconnects the connection
                reader.close()
                connection.disconnect()
                //returns the response as a string
                response.toString()
            }

            Log.d("API", "Response: $response")

            //grabs the json and parses it to extract clubs data
            val clubs = parsejsonclubs(response)
            //returns the clubs
            onComplete(clubs)
        } catch (e: Exception) {
            Log.e("NetworkError", "Error fetching clubs: ${e.message}", e)
            onComplete(emptyList())
        }
    }

    fun parsejsonclubs(json: String): List<footballclubsbyleague> {
        // initialize an empty mutable list to hold the parsed clubs
        val clubs = mutableListOf<footballclubsbyleague>()

        try {
            //create jsonobect from json string
            val jsonobject = JSONObject(json)
            //gets the teams array from the json object
            val jsonarray = jsonobject.getJSONArray("teams")

            //loops through the teams array
            for (i in 0 until jsonarray.length()) {
                val clubJson = jsonarray.getJSONObject(i)

                //extract the details from the clubs
                val club = footballclubsbyleague(
                    idTeam = clubJson.getString("idTeam"),
                    Name = clubJson.getString("strTeam"),
                    strTeamShort = clubJson.getString("strTeamShort"),
                    strAlternate = clubJson.getString("strAlternate"),
                    intFormedYear = clubJson.getString("intFormedYear"),
                    strLeague = clubJson.getString("strLeague"),
                    idLeague = clubJson.getString("idLeague"),
                    strStadium = clubJson.getString("strStadium"),
                    strKeywords = clubJson.getString("strKeywords"),
                    strStadiumThumb = clubJson.getString("strStadiumThumb"),
                    strStadiumLocation = clubJson.getString("strStadiumLocation"),
                    intStadiumCapacity = clubJson.getString("intStadiumCapacity"),
                    strWebsite = clubJson.getString("strWebsite"),
                    strTeamJersey = clubJson.getString("strTeamJersey"),
                    strTeamLogo = clubJson.getString("strTeamLogo"),
                )
                //adds the club to the list of clubs
                clubs.add(club)
            }
        } catch (e: JSONException) {
            Log.e("JSONError", "Error parsing JSON: ${e.message}", e)
        }

        //returns the list of clubs
        return clubs
    }


    @Composable
    fun searchforclubs(navController: NavHostController) {

        //variables used to hold and remember
        var textfieldsearch by remember { mutableStateOf("") }
        val clubs = remember { mutableStateOf<List<Club>>(emptyList()) }
        val coroutinescope = rememberCoroutineScope()

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            //textfield to put random letters inside
            TextField(
                value = textfieldsearch,
                onValueChange = { textfieldsearch = it },
                label = { Text("Enter A Text Of letters") },
                modifier = Modifier.fillMaxWidth()
            )
            //button to search for anything with those letters
            Button(
                onClick = {
                    //launches the coroutine to perform the search button
                    coroutinescope.launch {
                        withContext(Dispatchers.IO) {
                            //search for clubs based on the search query of the text field
                            val searchingclubs = db.ClubsDAO().searchclubswithquery("%$textfieldsearch%")
                            //update clubs list
                            clubs.value = searchingclubs
                        }
                    }
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Search")
            }

            LazyColumn {
                //iterate over the list of clubs and display club details
                items(clubs.value) { club ->
                    Column(
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {

                            }
                    ) {
                        Text(text = "Name: ${club.name}", fontWeight = FontWeight.Bold)
                        Text(text = "Short Name: ${club.shortName}")
                        Text(text = "Alternate Name: ${club.alternate}")
                        Text(text = "Formed Year: ${club.formedYear}")
                        Text(text = "League: ${club.league}")
                        Text(text = "Stadium: ${club.stadium}")
                        Text(text = "Keywords: ${club.keywords}")
                        Text(text = "Stadium Location: ${club.stadiumLocation}")
                        Text(text = "Stadium Capacity: ${club.stadiumCapacity}")
                        Text(text = "Website: ${club.website}")
                        Text(text = "Team Jersey: ${club.teamJersey}")
                        Text(text = "Team Logo: ${club.teamLogo}")

                        //display club logo if available
                        if (club.teamLogo.isNotEmpty()) {
                            val painter = rememberImagePainter(
                                data = club.teamLogo,
                                builder = {

                                }
                            )
                            Image(
                                painter = painter,
                                contentDescription = "Club Logo",
                                modifier = Modifier.size(100.dp)
                            )
                        } else {
                            Text(text = "No Logo Available")
                        }

                    }
                    Divider() //divder to divide the clubs
                }
            }
        }
    }


}




