package com.example.demo.controller;

import com.example.demo.model.SearchQuery;
import com.example.demo.service.SearchService;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

  @PostMapping("/download")
  public ResponseEntity<InputStreamResource> exportCsv(@RequestParam("results") String resultsString) {
    List<String> results = Arrays.asList(resultsString.split(","));

    StringBuilder csvData = new StringBuilder();
    for (String result : results) {
      csvData.append(result).append("\n");
    }

    try {
      // Create InputStreamResource from StringBuilder data
      InputStreamResource fileInputStream = new InputStreamResource(
          new ByteArrayInputStream(csvData.toString().getBytes())
      );

      // Return the CSV as a downloadable file
      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=results.csv")
          .contentType(MediaType.parseMediaType("text/csv"))
          .body(fileInputStream);
    } catch (Exception e) {
      e.printStackTrace();
      return ResponseEntity.internalServerError().build();
    }
  }
}