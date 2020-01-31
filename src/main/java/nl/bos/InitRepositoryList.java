package nl.bos;

import com.documentum.fc.client.IDfDocbaseMap;
import javafx.concurrent.Task;
import nl.bos.utils.Calculations;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.logging.Logger;

public class InitRepositoryList extends Task<Boolean> {
    private static final Logger LOGGER = Logger.getLogger(InitRepositoryList.class.getName());

    @Override
    protected Boolean call() throws Exception {
    	Instant start = Instant.now();
        //Repository repository = Repository.getInstance();
        
//        IDfDocbaseMap repositoryMap = repository.obtainRepositoryMap();
//        for (int i = 0; i < repositoryMap.getDocbaseCount(); i++) {
//            LOGGER.info(MessageFormat.format("Repository {0}: {1}", i + 1, repositoryMap.getDocbaseName(i)));
//        }
        Instant end = Instant.now();
        LOGGER.info(Calculations.getDurationInSeconds(start, end));
        return true;
    }
}
