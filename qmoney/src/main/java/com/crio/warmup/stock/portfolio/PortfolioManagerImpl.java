
package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.crio.warmup.stock.quotes.StockQuotesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {


  private  StockQuotesService stockQuotesService ;

  private RestTemplate restTemplate;

  // private String getToken() {
  //   return "97c5e06e483aa89f0b67373c3a80db1ac13b69f2";
  // }

  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  @Deprecated


  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public PortfolioManagerImpl(StockQuotesService stockQuotesService)
  {
    this.stockQuotesService = stockQuotesService ;
  }


  //TODO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from main anymore.
  //    Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  //    into #calculateAnnualizedReturn function here and ensure it follows the method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required further as our
  //    clients will take care of it, going forward.

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command below:
  // ./gradlew test --tests PortfolioManagerTest

  //CHECKSTYLE:OFF


  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades,
  LocalDate endDate){
    List<AnnualizedReturn> ans = new ArrayList<>();
    try{
      for (PortfolioTrade portfolioTrade : portfolioTrades) {
        List<Candle> candleList = stockQuotesService.getStockQuote(portfolioTrade.getSymbol(), portfolioTrade.getPurchaseDate(), endDate);
        Double buyPrice = getOpeningPriceOnStartDate(candleList);
        Double sellPrice = getClosingPriceOnEndDate(candleList);
        AnnualizedReturn annualizedReturn = calculateAnnualizedReturns(endDate, portfolioTrade, buyPrice, sellPrice);
        ans.add(annualizedReturn);
      }
    }
   catch(Exception exception){
    exception.printStackTrace();
   }
    Collections.sort(ans, getComparator());
    return ans;
  }
 public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate,
      PortfolioTrade trade, Double buyPrice, Double sellPrice) {
      Double totalReturns = (sellPrice-buyPrice)/buyPrice;
      String symbol = trade.getSymbol();
      LocalDate purchaseDate = trade.getPurchaseDate();
      Double totalYears = calculateYearsBetweenDates(purchaseDate, endDate);
      Double annualizedReturns = Math.pow((1+totalReturns),1/totalYears) -1;
      return new AnnualizedReturn(symbol, annualizedReturns, totalReturns);
  }

  public static Double calculateYearsBetweenDates(LocalDate startDate, LocalDate endDate){
      long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
      Double totalYears = daysBetween/365.0;
      return totalYears;
  }
  public static Double getOpeningPriceOnStartDate(List<Candle> candles) {
    return candles.get(0).getOpen();
 }


 public static Double getClosingPriceOnEndDate(List<Candle> candles) {
    return candles.get(candles.size()-1).getClose();
 }
 
  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo third-party APIs to a separate function.
  //  Remember to fill out the buildUri function and use that.


  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException,StockQuoteServiceException {
      // List<Candle> ans = new ArrayList<>();
      // String url = buildUri(symbol, from, to);
      // TiingoCandle[] response = restTemplate.getForObject(url, TiingoCandle[].class);
      // ans = Arrays.asList(response);
      // return ans;
      return stockQuotesService.getStockQuote(symbol, from, to);
  }

  // protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
  //   String token = getToken();
  //   String uriTemplate = "https:api.tiingo.com/tiingo/daily/$SYMBOL/prices?"
  //                       + "startDate="+startDate+"&endDate="+endDate+"&token="+token;
  //   return uriTemplate;
  // }


  // ¶TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Modify the function #getStockQuote and start delegating to calls to
  //  stockQuoteService provided via newly added constructor of the class.
  //  You also have a liberty to completely get rid of that function itself, however, make sure
  //  that you do not delete the #getStockQuote function.




}
