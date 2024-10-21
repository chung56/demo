package com.example.demo.service;

import java.io.IOException;
import java.util.List;

public interface SearchService {

  List<String> search(String query) throws IOException;

}
