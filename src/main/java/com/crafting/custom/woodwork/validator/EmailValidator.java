package com.crafting.custom.woodwork.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.*;
import org.xbill.DNS.Record;


public class EmailValidator {
	private static final Logger log = LoggerFactory.getLogger(EmailValidator.class);
	
	public static boolean hasValidMXRecords(String email) {
		log.info("inside hasValidMXRecords()....");
		 try {
	            String domain = email.substring(email.indexOf("@") + 1);

	            // Perform MX record lookup
	            Lookup lookup = new Lookup(domain, Type.MX);
	            lookup.run();
	            Record[] records = lookup.getAnswers(); // ✅ Correct Type

	            // Check if MX records exist
	            return records != null && records.length > 0;
	        } catch (TextParseException e) {
	        	log.info("Invalid email domain: " + e.getMessage());
	        } catch (Exception e) {
	        	log.info("Error checking MX records: " + e.getMessage());
	        }
	        return false;
    }

}
