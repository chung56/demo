package com.example.demo.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class SearchServicempl implements SearchService {

  @Override
  public List<String> search(String query) throws IOException {
    List<String> searchResults = new ArrayList<>();
    String url = "https://www.google.com/search?q=" + query + "&num=10";
    Document doc = Jsoup.connect(url).get();

    // Update the selector to capture the search results correctly
    Elements results = doc.select("h3"); // This will select the titles of the search results

    for (Element result : results) {
      String title = result.text();
      String link = result.parent().attr("href");
      // Check if the link is valid
      if (!link.isEmpty()) {
        searchResults.add(title + " - " + link);
      }
    }
    return searchResults;
  }
}
