package nl.tudelft.ewi.git.client;

import nl.tudelft.ewi.git.models.*;

/**
 * The Branch backend can be used for all git server interactions with a branch
 * @author Jan-Willem Gmelig Meyling
 */
public interface Branch extends Comparable<Branch> {

    /**
     * Fetch the diff for this branch
     * @return the diff
     * @throws GitClientException if an GitClientException occurs
     */
    DiffModel diff() throws GitClientException;

    /**
     * Fetch the diff for this branch
     * @return the diff with blame information
     * @throws GitClientException if an GitClientException occurs
     */
    DiffBlameModel diffBlame() throws GitClientException;

    /**
     * @param skip amount of commits to skip
     * @param limit limit
     * @return a sublist of commits for this branch
     * @throws GitClientException if an GitClientException occurs
     */
    CommitSubList retrieveCommits(int skip, int limit) throws GitClientException;

    /**
     * @return the name for this branch
     */
    String getName();

    /**
     * @return amount of commits that this branch is ahead of the master
     */
    Integer getAhead();

    /**
     * @return amount of commits that this branch is behind of the master
     */
    Integer getBehind();

    /**
     * @return the commit for the branch
     */
    CommitModel getCommit();

    /**
     * @return simple name for the branch
     */
    String getSimpleName();

    /**
     * Merge this branch into the master
     * @param message Message for merge commit
     * @param user Committer name
     * @param email Committer email
     * @return a MergeResponse
     * @throws GitClientException if an GitClientException occurs
     */
    MergeResponse merge(String message, String user, String email) throws GitClientException;

}