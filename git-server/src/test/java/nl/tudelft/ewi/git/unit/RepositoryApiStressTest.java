package nl.tudelft.ewi.git.unit;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import nl.tudelft.ewi.git.models.CreateRepositoryModel;
import nl.tudelft.ewi.git.models.DetailedRepositoryModel;
import nl.tudelft.ewi.git.models.RepositoryModel.Level;
import nl.tudelft.ewi.git.web.CucumberModule;
import nl.tudelft.ewi.git.web.api.BranchApi;
import nl.tudelft.ewi.git.web.api.RepositoriesApi;
import nl.tudelft.ewi.git.web.api.RepositoryApi;
import nl.tudelft.ewi.git.web.api.Transformers;
import nl.tudelft.ewi.gitolite.repositories.PathRepositoriesManager;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.jukito.JukitoRunner;
import org.jukito.UseModules;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author Jan-Willem Gmelig Meyling
 */
@Slf4j
@RunWith(JukitoRunner.class)
@UseModules(CucumberModule.class)
public class RepositoryApiStressTest {

    @Inject
    Transformers transformers;
    @Inject
    PathRepositoriesManager pathRepositoriesManager;
    @Inject
    RepositoriesApi repositoriesApi;
    DetailedRepositoryModel detailedRepositoryModel;

    private CreateRepositoryModel crm;

    public static final int threadCount = 200;

    @Before
    public void setUp() throws GitAPIException, IOException {
        crm = new CreateRepositoryModel();
        crm.setTemplateRepository("https://github.com/SERG-Delft/jpacman-template.git");
        crm.setName("stress-test");
        crm.setPermissions(ImmutableMap.of("me", Level.ADMIN));
        detailedRepositoryModel = repositoriesApi.createRepository(crm);
    }

    @Ignore
    @Test
    public void testGitBlameWithSubmoduleThreaded() throws Exception {
        Runnable r = () -> {

        };

        List<Thread> thr = IntStream.range(0, threadCount).mapToObj(i -> new Thread(r)).collect(Collectors.toList());
        thr.forEach(Thread::start);
        thr.forEach((thread) -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

    }

    @Test
    public void testGitBlameWithSubmodule() {
        RepositoryApi repositoryApi = repositoriesApi.getRepository(detailedRepositoryModel.getName());
        String[] masters = repositoryApi.getBranch("master").getCommit().get().getParents();
        recursion(masters);
    }

    public void recursion(String[] parents) {
        for (String parent : parents) {
            RepositoryApi repositoryApi = repositoriesApi.getRepository(detailedRepositoryModel.getName());
            recursion(repositoryApi.getCommit(parent).get().getParents());
        }
    }
}
