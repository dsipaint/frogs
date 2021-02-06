package jems;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Random;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter
{
	static final String subscriptionKey = "",
			host = "https://api.bing.microsoft.com",
			path = "/v7.0/images/search",
			searchTerm = "frog";
	
	public void onGuildMessageReceived(GuildMessageReceivedEvent e)
	{
		String msg = e.getMessage().getContentRaw();
		String[] args = msg.split(" ");
		
		if(args[0].equalsIgnoreCase(Main.PREFIX + "frog"))
		{
			try
			{
				EmbedBuilder eb = new EmbedBuilder()
						.setTitle("Here's a frog for you!")
						.setColor(65280)
						.setImage(getRandomFrog());
				
				e.getChannel().sendMessage(eb.build()).queue();
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
	}
	
	public static String getRandomFrog() throws IOException
	{
		// construct the search request URL (in the form of endpoint + query string)
		URL url = new URL(host + path + "?q=" +  URLEncoder.encode(searchTerm, "UTF-8"));
		HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
		connection.setRequestProperty("Ocp-Apim-Subscription-Key", subscriptionKey);
		
		// receive JSON body
		InputStream stream = connection.getInputStream();
		Scanner sc = new Scanner(stream);
		String response = sc.useDelimiter("\\A").next();
		
		stream.close();
		JsonObject json = JsonParser.parseString(response).getAsJsonObject();
		//get the first image result from the JSON object, along with the total
		//number of images returned by the Bing Image Search API.
		//String total = json.get("totalEstimatedMatches").getAsString();
		
		JsonArray results = json.getAsJsonArray("value");
		JsonObject random_result = (JsonObject) results.get(new Random().nextInt(results.size()));
		
		
		String resultURL = random_result.get("thumbnailUrl").getAsString();
		sc.close();
		return resultURL;
	}
}
