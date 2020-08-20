package monash.edu.git;

public class GitLabRepository {
    private String id;

    public GitLabRepository(String id)
    {
        this.id=id;

        String repoUrl="https://gitlab.com/api/v4/projects/"+id;
        constructRepoCommits(repoUrl);


    }

    public void constructRepoCommits(String repoUrl)
    {

    }

}
