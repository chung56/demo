package com.example.demo.controller;

import com.example.demo.model.SearchQuery;
import com.example.demo.service.SearchService;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchController {

  private final SearchService searchService;

  public SearchController(SearchService searchService) {
    this.searchService = searchService;
  }

  @GetMapping("/")
  public String showSearchForm(Model model) {
    model.addAttribute("searchQuery", new SearchQuery());
    return "index";
  }

  @PostMapping("/search")
  public String search(@ModelAttribute("searchQuery") SearchQuery searchQuery, Model model) {
    String query = searchQuery.getQuery();
    model.addAttribute("searchQuery", searchQuery);

    try {
      List<String> searchResults = searchService.search(query);
      model.addAttribute("results", searchResults);
      model.addAttribute("resultsString", String.join(",", searchResults));
    } catch (IOException e) {
      e.printStackTrace();
      model.addAttribute("error", "A search error occurred.");
    }
    return "index";
  }

  @PostMapping("/export")
  public String createCsvFile(@RequestParam("results") String resultsString, @ModelAttribute("searchQuery") SearchQuery searchQuery, Model model) {
    String desktopPath = System.getProperty("user.home") + "/Desktop";
    String csvFilePath = desktopPath + "/results.csv";

    List<String> results = Arrays.asList(resultsString.split(","));

    try (FileWriter fileWriter = new FileWriter(csvFilePath)) {
      for (String result : results) {
        fileWriter.write(result + "\n");
      }
      model.addAttribute("exportMessage", "Export successful! File results.csv has been saved.");
    } catch (IOException e) {
      e.printStackTrace();
      model.addAttribute("error", "Export not successful!");
    }
    return "index"; // Redirect back to the index page
  }
}