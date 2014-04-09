package nl.tudelft.ewi.devhub.server.backend;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nl.tudelft.ewi.devhub.server.database.entities.User;
import nl.tudelft.ewi.devhub.server.web.errors.ApiError;
import nl.tudelft.ewi.git.client.GitServerClient;
import nl.tudelft.ewi.git.models.SshKeyModel;
import nl.tudelft.ewi.git.models.UserModel;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class SshKeyBackend {

	private static final String INVALID_KEY_NAME = "error.invalid-key-name";
	private static final String INVALID_KEY_CONTENTS = "error.invalid-key-contents";
	private static final String NAME_ALREADY_EXISTS = "error.name-alread-exists";
	private static final String DUPLICATE_KEY = "error.duplicate-key";
	private static final String NO_SUCH_KEY = "error.no-such-key";

	private final GitServerClient client;

	@Inject
	SshKeyBackend(GitServerClient client) {
		this.client = client;
	}

	public void createNewSshKey(User user, String name, String contents) throws ApiError {
		if (name == null || !name.matches("^[a-zA-Z0-9\\.]+$")) {
			throw new ApiError(INVALID_KEY_NAME);
		}
		if (contents == null || !contents.matches("^ssh-rsa\\s.+$")) {
			throw new ApiError(INVALID_KEY_CONTENTS);
		}
		
		UserModel userModel = client.users().ensureExists(user.getNetId());
		for (SshKeyModel sshKeyModel : userModel.getKeys()) {
			if (sshKeyModel.getName().equals(name)) {
				throw new ApiError(NAME_ALREADY_EXISTS);
			}
			if (sshKeyModel.getContents().equals(contents)) {
				throw new ApiError(DUPLICATE_KEY);
			}
		}
		
		SshKeyModel model = new SshKeyModel();
		model.setName(name);
		model.setContents(contents);
		client.users().sshKeys(userModel).registerSshKey(model);
	}
	
	public void deleteSshKey(User user, String name) throws ApiError {
		if (name == null || !name.matches("^[a-zA-Z0-9\\.]+$")) {
			throw new ApiError(INVALID_KEY_NAME);
		}
		
		SshKeyModel keyModel = null;
		UserModel userModel = client.users().ensureExists(user.getNetId());
		for (SshKeyModel sshKeyModel : userModel.getKeys()) {
			if (sshKeyModel.getName().equals(name)) {
				keyModel = sshKeyModel;
			}
		}
		
		if (keyModel == null) {
			throw new ApiError(NO_SUCH_KEY);
		}
		
		client.users().sshKeys(userModel).deleteSshKey(keyModel);
	}
	
	public List<SshKeyModel> listKeys(User user) {
		UserModel userModel = client.users().ensureExists(user.getNetId());
		List<SshKeyModel> keys = Lists.newArrayList(userModel.getKeys());
		Collections.sort(keys, new Comparator<SshKeyModel>() {
			@Override
			public int compare(SshKeyModel o1, SshKeyModel o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		
		return keys;
	}

}