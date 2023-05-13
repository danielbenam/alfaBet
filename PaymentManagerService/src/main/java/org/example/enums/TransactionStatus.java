package org.example.enums;

public enum TransactionStatus {

    SUCCESS, FAILED, NEED_MORE_INVESTIGATION, WAITING_TO_PERFORM, WAITING_TO_REPORT, WAITING_FOR_TRANSACTION_RESULT;

    public static TransactionStatus createFrom(String status) {
        if(status.equals("fail")) return TransactionStatus.FAILED;
        if(status.equals("success")) return TransactionStatus.SUCCESS;
        return TransactionStatus.NEED_MORE_INVESTIGATION;
    }
}
