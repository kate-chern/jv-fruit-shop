package core.basesyntax;

import core.basesyntax.dao.FruitDao;
import core.basesyntax.dao.FruitDaoImpl;
import core.basesyntax.model.Fruit;
import core.basesyntax.service.impl.DeleteFirstLine;
import core.basesyntax.service.impl.ProductParserImpl;
import core.basesyntax.service.impl.ReaderServiceImpl;
import core.basesyntax.service.impl.ReportServiceImpl;
import core.basesyntax.service.impl.WriterServiceImpl;
import core.basesyntax.strategy.BalanceHandler;
import core.basesyntax.strategy.OperationHandler;
import core.basesyntax.strategy.OperationStrategy;
import core.basesyntax.strategy.OperationStrategyImpl;
import core.basesyntax.strategy.PurchaseHandler;
import core.basesyntax.strategy.ReturnHandler;
import core.basesyntax.strategy.SupplyHandler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    private static final String INPUT_FILE
            = "src/main/java/core/basesyntax/resources/inputFile.csv";
    private static final String OUTPUT_FILE
            = "src/main/java/core/basesyntax/resources/reportFile.csv";
    private static final String OUTPUT_TITLE
            = "fruit,quantity";

    public static void main(String[] args) {
        FruitDao fruitDao = new FruitDaoImpl();
        OperationHandler operationHandlerBalance = new BalanceHandler(fruitDao);
        OperationHandler operationHandlerPurchase = new PurchaseHandler(fruitDao);
        OperationHandler operationHandlerReturn = new ReturnHandler(fruitDao);
        OperationHandler operationHandlerSupply = new SupplyHandler(fruitDao);

        Map<Fruit.Operation, OperationHandler> handlerMap = new HashMap<>();
        handlerMap.put(Fruit.Operation.BALANCE, operationHandlerBalance);
        handlerMap.put(Fruit.Operation.PURCHASE, operationHandlerPurchase);
        handlerMap.put(Fruit.Operation.RETURN, operationHandlerReturn);
        handlerMap.put(Fruit.Operation.SUPPLY, operationHandlerSupply);

        OperationStrategy operationStrategy = new OperationStrategyImpl(handlerMap);

        List<String> records = new ReaderServiceImpl().readFromFile((INPUT_FILE));
        records = new DeleteFirstLine().delete(records);

        new ProductParserImpl().parseAll(records)
                .forEach(e -> operationStrategy.get(e.getOperation())
                        .operate(e));

        records = new ReportServiceImpl(fruitDao).report(OUTPUT_TITLE);
        new WriterServiceImpl().writeToFile(records, OUTPUT_FILE);
    }
}