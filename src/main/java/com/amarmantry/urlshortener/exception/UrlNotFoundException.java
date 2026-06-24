package com.amarmantry.urlshortener.exception;


public class UrlNotFoundException extends  RuntimeException{
        public UrlNotFoundException(String message) {
            super(message);
        }
}
