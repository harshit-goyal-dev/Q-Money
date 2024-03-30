
package com.crio.warmup.stock.quotes;

import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.springframework.web.client.RestTemplate;

public class TiingoService implements StockQuotesService {

  private RestTemplate restTemplate;

  protected TiingoService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }


  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement getStockQuote method below that was also declared in the interface.

  // Note:
  // 1. You can move the code from PortfolioManagerImpl#getStockQuote inside newly created method.
  // 2. Run the tests using command below and make sure it passes.
  //    ./gradlew test --tests TiingoServiceTest

  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException, StockQuoteServiceException {
      List<Candle> ans = new ArrayList<>();
      String url = buildUri(symbol, from, to);
      String stringResponse = restTemplate.getForObject(url, String.class);
      if(stringResponse ==null || stringResponse=="")throw new StockQuoteServiceException("Failed to fetch reponse");

      // ObjectMapper objectMapper = new ObjectMapper();
      ObjectMapper objectmapper =  new ObjectMapper().registerModule(new JavaTimeModule());
      TiingoCandle[] response = objectmapper.readValue(stringResponse, TiingoCandle[].class);
      if(response ==null)throw new StockQuoteServiceException("Response is incorrectly formatted");

     // TiingoCandle[] response = restTemplate.getForObject(url, TiingoCandle[].class);
      ans = Arrays.asList(response);
      Collections.sort(ans,getComparator());
      return ans;
  }

  private Comparator<Candle> getComparator() {
    return Comparator.comparing(Candle::getDate);
  }
  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Write a method to create appropriate url to call the Tiingo API.

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    String token = getToken();
    String uriTemplate = "https://api.tiingo.com/tiingo/daily/"+symbol+"/prices?"
                        + "startDate="+startDate+"&endDate="+endDate+"&token="+token;
    //System.out.println(uriTemplate);
    return uriTemplate;
  }
  private String getToken() {
    return "97c5e06e483aa89f0b67373c3a80db1ac13b69f2";
  }





  // TODO: CRIO_TASK_MODULE_EXCEPTIONS
  //  1. Update the method signature to match the signature change in the interface.
  //     Start throwing new StockQuoteServiceException when you get some invalid response from
  //     Tiingo, or if Tiingo returns empty results for whatever reason, or you encounter
  //     a runtime exception during Json parsing.
  //  2. Make sure that the exception propagates all the way from
  //     PortfolioManager#calculateAnnualisedReturns so that the external user's of our API
  //     are able to explicitly handle this exception upfront.

  //CHECKSTYLE:OFF


}
