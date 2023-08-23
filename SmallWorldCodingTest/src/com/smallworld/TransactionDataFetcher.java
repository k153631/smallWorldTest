package com.smallworld;

import java.io.FileReader;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


public class TransactionDataFetcher {

    private JSONArray transactions;

    public TransactionDataFetcher(String jsonFilePath) {
        try {
            JSONParser parser = new JSONParser();
            FileReader reader = new FileReader(jsonFilePath);
            transactions = (JSONArray) parser.parse(reader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the sum of the amounts of all transactions
     */
    public double getTotalTransactionAmount() {
        double totalAmount = 0.0;
        for (Object obj : transactions) {
            JSONObject transaction = (JSONObject) obj;
            totalAmount += (double) transaction.get("amount");
        }
        return totalAmount;
    }

    /**
     * Returns the sum of the amounts of all transactions sent by the specified client
     */
    public double getTotalTransactionAmountSentBy(String senderFullName) {
        double totalAmount = 0.0;
        for (Object obj : transactions) {
            JSONObject transaction = (JSONObject) obj;
            String sender = (String) transaction.get("senderFullName");
            if (sender.equals(senderFullName)) {
                totalAmount += (double) transaction.get("amount");
            }
        }
        return totalAmount;
    }

    /**
     * Returns the highest transaction amount
     */
    public double getMaxTransactionAmount() {
        double maxAmount = Double.MIN_VALUE;
        for (Object obj : transactions) {
            JSONObject transaction = (JSONObject) obj;
            double amount = (double) transaction.get("amount");
            if (amount > maxAmount) {
                maxAmount = amount;
            }
        }
        return maxAmount;
    }

    /**
     * Counts the number of unique clients that sent or received a transaction
     */
    public long countUniqueClients() {
        Set<String> uniqueClients = new HashSet<>();
        for (Object obj : transactions) {
            JSONObject transaction = (JSONObject) obj;
            uniqueClients.add((String) transaction.get("senderFullName"));
            uniqueClients.add((String) transaction.get("beneficiaryFullName"));
        }
        return uniqueClients.size();
    }

    /**
     * Returns whether a client (sender or beneficiary) has at least one transaction with a compliance
     * issue that has not been solved
     */
    public boolean hasOpenComplianceIssues(String clientFullName) {
        for (Object obj : transactions) {
            JSONObject transaction = (JSONObject) obj;
            String sender = (String) transaction.get("senderFullName");
            String beneficiary = (String) transaction.get("beneficiaryFullName");
            if ((sender.equals(clientFullName) || beneficiary.equals(clientFullName))
                    && !(boolean) transaction.get("issueSolved")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns all transactions indexed by beneficiary name
     */
    public Map<String, List<JSONObject>> getTransactionsByBeneficiaryName() {
        Map<String, List<JSONObject>> transactionsByBeneficiary = new HashMap<>();
        for (Object obj : transactions) {
            JSONObject transaction = (JSONObject) obj;
            String beneficiary = (String) transaction.get("beneficiaryFullName");
            if (!transactionsByBeneficiary.containsKey(beneficiary)) {
                transactionsByBeneficiary.put(beneficiary, new ArrayList<>());
            }
            transactionsByBeneficiary.get(beneficiary).add(transaction);
        }
        return transactionsByBeneficiary;
    }

    /**
     * Returns the identifiers of all open compliance issues
     */
    public Set<Integer> getUnsolvedIssueIds() {
        Set<Integer> unsolvedIssueIds = new HashSet<>();
        for (Object obj : transactions) {
            JSONObject transaction = (JSONObject) obj;
            boolean issueSolved = (boolean) transaction.get("issueSolved");
            if (!issueSolved && transaction.get("issueId") != null) {
                unsolvedIssueIds.add(((Long) transaction.get("issueId")).intValue());
            }
        }
        return unsolvedIssueIds;
    }

    /**
     * Returns a list of all solved issue messages
     */
    public List<String> getAllSolvedIssueMessages() {
        List<String> solvedIssueMessages = new ArrayList<>();
        for (Object obj : transactions) {
            JSONObject transaction = (JSONObject) obj;
            boolean issueSolved = (boolean) transaction.get("issueSolved");
            String issueMessage = (String) transaction.get("issueMessage");
            if (issueSolved && issueMessage != null) {
                solvedIssueMessages.add(issueMessage);
            }
        }
        return solvedIssueMessages;
    }

    /**
     * Returns the 3 transactions with highest amount sorted by amount descending
     */
    public List<JSONObject> getTop3TransactionsByAmount() {
        List<JSONObject> sortedTransactions = new ArrayList<>(transactions);
        sortedTransactions.sort((t1, t2) -> Double.compare((double) t2.get("amount"), (double) t1.get("amount")));
        return sortedTransactions.subList(0, Math.min(3, sortedTransactions.size()));
    }


    /**
     * Returns the sender with the most total sent amount
     */
    public Optional<JSONObject> getTopSender() {
        Map<String, Double> senderToAmountMap = new HashMap<>();
        JSONParser parser = new JSONParser();

        for (Object obj : transactions) {
            JSONObject transaction;
            try {
                transaction = (JSONObject) parser.parse(obj.toString());
                String sender = (String) transaction.get("senderFullName");
                double amount = (double) transaction.get("amount");
                senderToAmountMap.put(sender, senderToAmountMap.getOrDefault(sender, 0.0) + amount);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Optional<Map.Entry<String, Double>> maxEntry = senderToAmountMap.entrySet()
                .stream()
                .max(Comparator.comparing(Map.Entry::getValue));

        if (maxEntry.isPresent()) {
            String topSender = maxEntry.get().getKey();
            return transactions.stream()
                    .map(obj -> {
                        try {
                            return (JSONObject) parser.parse(obj.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    })
                    .filter(transaction -> transaction != null)
                    .findFirst();
        }

        return Optional.empty();
    }



}
