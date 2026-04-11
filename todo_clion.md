creates account when document does not exist
rejects duplicate document
normalizes document if you chose to normalize
returns expected domain/result object

Get account service

returns account when id exists
returns not found / throws specific exception when id does not exist
rejects invalid id if that validation is in service layer

Transaction creation service

creates payment as positive
creates purchase/installment/withdrawal as negative
rejects invalid account id
rejects invalid operation type
rejects zero amount
rejects negative input amount if your rule is “client always sends positive raw amount”
stores normalized amount with correct sign