package nl.tudelft.ewi.devhub.webtests;

import static org.junit.Assert.*;

import java.util.List;

import nl.tudelft.ewi.devhub.webtests.utils.WebTest;
import nl.tudelft.ewi.devhub.webtests.views.DiffView;
import nl.tudelft.ewi.devhub.webtests.views.DiffView.DiffElement;
import nl.tudelft.ewi.devhub.webtests.views.ProjectView;
import nl.tudelft.ewi.devhub.webtests.views.ProjectView.Commit;
import nl.tudelft.ewi.git.client.GitServerClientMock;
import nl.tudelft.ewi.git.models.BranchModel;
import nl.tudelft.ewi.git.models.CommitModel;
import nl.tudelft.ewi.git.models.DetailedCommitModel;
import nl.tudelft.ewi.git.models.DiffContext;
import nl.tudelft.ewi.git.models.DiffLine;
import nl.tudelft.ewi.git.models.DiffModel;
import nl.tudelft.ewi.git.models.DiffModel.Type;
import nl.tudelft.ewi.git.models.MockedRepositoryModel;
import nl.tudelft.ewi.git.models.UserModel;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Lists;

public class ProjectTest extends WebTest {
	
	public static final String COMMIT_ID = "6f69819c39b87566a65a2a005a6553831f6d7e7c";
	public static final String COMMIT_MESSAGE = "Initial commit";
	
	private static GitServerClientMock gitServerClient;
	private static MockedRepositoryModel repository;
	private static UserModel user;
	private static DetailedCommitModel commit;
	
	@BeforeClass
	public static void setUpRepository() throws Exception {
		gitServerClient = getGitServerClient();
		user = gitServerClient.users().ensureExists(NET_ID);
		repository = gitServerClient.repositories().retrieve("courses/ti1705/group-1");
		commit = createInitialCommit(repository);
	}
	
	private static DetailedCommitModel createInitialCommit(MockedRepositoryModel repository) {
		DetailedCommitModel commit = new DetailedCommitModel();
		commit.setAuthor(user.getName());
		commit.setCommit(COMMIT_ID);
		commit.setParents(new String[] {});
		commit.setTime(System.currentTimeMillis());
		commit.setFullMessage(COMMIT_MESSAGE);
		repository.addCommit(commit);

		BranchModel branch = new BranchModel();
		branch.setCommit(COMMIT_ID);
		branch.setName("refs/remotes/origin/master");
		repository.addBranch(branch);
		
		return commit;
	}
	
	/**
	 * <h1>Opening a project overview .</h1>
	 * 
	 * Given that:
	 * <ol>
	 *   <li>I am successfully logged in.</li>
	 *   <li>I have a project.</li>
	 *   <li>There is a commit in the project.</li>
	 * </ol>
	 * When:
	 * <ol>
	 *   <li>I click on a project in the project list.</li>
	 * </ol>
	 * Then:
	 * <ol>
	 *   <li>I am redirected to the project page.</li>
	 * </ol>
	 */
	@Test
	public void testListCommits() {
		ProjectView view = openLoginScreen()
				.login(NET_ID, PASSWORD)
				.toProjectsView()
				.listMyProjects()
				.get(0).click();
		
		List<Commit> commits = view.listCommits();
		List<CommitModel> expected = gitServerClient.repositories().listCommits(repository);
		assertEquals(expected.size(), commits.size());
		
		for(int i = 0, s = expected.size(); i < s; i++) {
			Commit commit = commits.get(i);
			CommitModel model = expected.get(i);
			assertEquals(commit.getAuthor(), model.getAuthor());
			assertEquals(commit.getMessage(), model.getMessage());
		}		
	}
	
	/**
	 * <h1>Opening a project overview .</h1>
	 * 
	 * Given that:
	 * <ol>
	 *   <li>I am successfully logged in.</li>
	 *   <li>I have a project.</li>
	 *   <li>There is a commit in the project.</li>
	 * </ol>
	 * When:
	 * <ol>
	 *   <li>I click on a project in the project list.</li>
	 * </ol>
	 * Then:
	 * <ol>
	 *   <li>I am redirected to the diff page.</li>
	 * </ol>
	 */
	@Test
	public void testViewCommitDiffEmpty() {
		DiffView view = openLoginScreen()
				.login(NET_ID, PASSWORD)
				.toProjectsView()
				.listMyProjects()
				.get(0).click()
				.listCommits()
				.get(0).click();
		List<DiffElement> list = view.listDiffs();
		assertEquals(commit.getAuthor(), view.getAuthorHeader());
		assertEquals(commit.getMessage(), view.getMessageHeader());		
		assertTrue("Expected empty list", list.isEmpty());
	}
	

	/**
	 * <h1>Opening a project overview .</h1>
	 * 
	 * Given that:
	 * <ol>
	 *   <li>I am successfully logged in.</li>
	 *   <li>I have a project.</li>
	 *   <li>There is a commit in the project.</li>
	 * </ol>
	 * When:
	 * <ol>
	 *   <li>I click on a project in the project list.</li>
	 * </ol>
	 * Then:
	 * <ol>
	 *   <li>I am redirected to the diff page.</li>
	 * </ol>
	 */
	@Test
	public void testViewCommitDiff() {
		DiffLine a = new DiffLine();
		a.setContent("A readme file with a bit of contents");
		a.setType(DiffLine.Type.CONTEXT);
		DiffLine b = new DiffLine();
		b.setContent("Now we've altered the readme a bit to work on the diffs");
		b.setType(DiffLine.Type.ADDED);
		
		DiffContext diffContext = new DiffContext();
		diffContext.setOldStart(1);
		diffContext.setOldEnd(1);
		diffContext.setNewStart(1);
		diffContext.setNewEnd(2);
		diffContext.setDiffLines(Lists.<DiffLine> newArrayList(a, b));
		
		DiffModel model = new DiffModel();
		model.setOldPath("the/old/path");
		model.setNewPath("the/new/path");
		model.setType(Type.MODIFY);
		model.setDiffContexts(Lists.<DiffContext> newArrayList(diffContext));
		
		gitServerClient.repositories().setListDiffs(Lists.<DiffModel> newArrayList(model));
		
		DiffView view = openLoginScreen()
				.login(NET_ID, PASSWORD)
				.toProjectsView()
				.listMyProjects()
				.get(0).click()
				.listCommits()
				.get(0).click();
		
		List<DiffElement> list = view.listDiffs();
		DiffElement result = list.get(0);

		assertEquals(commit.getAuthor(), view.getAuthorHeader());
		assertEquals(commit.getMessage(), view.getMessageHeader());
		result.assertEqualTo(model);
	}
	
}