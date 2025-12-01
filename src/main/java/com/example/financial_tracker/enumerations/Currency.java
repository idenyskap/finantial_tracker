package com.example.financial_tracker.enumerations;

public enum Currency {
  USD("US Dollar", "$", 2),
  EUR("Euro", "€", 2),
  GBP("British Pound", "£", 2),
  JPY("Japanese Yen", "¥", 0),
  CAD("Canadian Dollar", "C$", 2),
  AUD("Australian Dollar", "A$", 2),
  CHF("Swiss Franc", "CHF", 2),
  CNY("Chinese Yuan", "¥", 2),
  UAH("Ukrainian Hryvnia", "₴", 2),
  SEK("Swedish Krona", "kr", 2),
  NZD("New Zealand Dollar", "NZ$", 2),
  MXN("Mexican Peso", "$", 2),
  SGD("Singapore Dollar", "S$", 2),
  HKD("Hong Kong Dollar", "HK$", 2),
  NOK("Norwegian Krone", "kr", 2),
  KRW("South Korean Won", "₩", 0),
  TRY("Turkish Lira", "₺", 2),
  INR("Indian Rupee", "₹", 2),
  BRL("Brazilian Real", "R$", 2),
  ZAR("South African Rand", "R", 2),
  PLN("Polish Zloty", "zł", 2),
  THB("Thai Baht", "฿", 2),
  MYR("Malaysian Ringgit", "RM", 2);

  private final String displayName;
  private final String symbol;
  private final int decimalPlaces;

  Currency(String displayName, String symbol, int decimalPlaces) {
    this.displayName = displayName;
    this.symbol = symbol;
    this.decimalPlaces = decimalPlaces;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getSymbol() {
    return symbol;
  }

  public int getDecimalPlaces() {
    return decimalPlaces;
  }
}
