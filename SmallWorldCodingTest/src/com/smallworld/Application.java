package com.smallworld;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;

public class Application {

	public static void main(String[] args) {

		
		String currentDirectory = System.getProperty("user.dir");
        System.out.println("Current working directory: " + currentDirectory+"\\"+"transactions.json");
        TransactionDataFetcher transDataFetObj = new TransactionDataFetcher(currentDirectory+"\\"+"transactions.json");
        System.out.println("Total Amount: "+ transDataFetObj.getTotalTransactionAmount());
        System.out.println("Total Amount Sent by Specific Sender: "+transDataFetObj.getTotalTransactionAmountSentBy("Aunt Polly"));
        System.out.println("Max Amount from Data: "+transDataFetObj.getMaxTransactionAmount());
        System.out.println("Count of Unique Senders and Benificiaries: "+transDataFetObj.countUniqueClients());
        System.out.println("At least one transaction with a compliance issue: " + (transDataFetObj.hasOpenComplianceIssues("Aunt Polly") ? "Yes" : "No"));
        
        System.out.println("----------------------------------------------------------------------------------------------------------------------");
        Map<String, List<JSONObject>> transactionsByBeneficiary = transDataFetObj.getTransactionsByBeneficiaryName();
        for (Map.Entry<String, List<JSONObject>> entry : transactionsByBeneficiary.entrySet()) {
            String beneficiaryName = entry.getKey();
            List<JSONObject> transactions = entry.getValue();
            
            System.out.println("Beneficiary: " + beneficiaryName);
            for (JSONObject transaction : transactions) {
                System.out.println("Transaction: " + transaction.toJSONString());
            }
        }
        System.out.println("----------------------------------------------------------------------------------------------------------------------\n\n");

        System.out.println("########################################################################################################################");
        Set<Integer> unsolvedIssueIds = transDataFetObj.getUnsolvedIssueIds();
        System.out.println("Unsolved Issue IDs:");
        for (Integer issueId : unsolvedIssueIds) {
            System.out.println(issueId);
        }
        System.out.println("########################################################################################################################\n\n");
        

        System.out.println("########################################################################################################################");
        List<String> solvedIssueMessages = transDataFetObj.getAllSolvedIssueMessages();
        System.out.println("Solved Issue Messages:");
        for (String issueMessage : solvedIssueMessages) {
            System.out.println(issueMessage);
        }
        System.out.println("########################################################################################################################\n\n");
        
        System.out.println("########################################################################################################################");
        List<JSONObject> top3Transactions = transDataFetObj.getTop3TransactionsByAmount();
        System.out.println("Top 3 Transactions by Amount:");
        for (JSONObject transaction : top3Transactions) {
            System.out.println(transaction.toJSONString());
        }
        System.out.println("########################################################################################################################\n\n");

        System.out.println("########################################################################################################################");
        System.out.println("Top Sender");
        System.out.println(transDataFetObj.getTopSender());
        System.out.println("########################################################################################################################");
        
	}

}
