package ui;

import table.UserModel;

public class Session {
	private static UserModel currentUser = null;

	public static void setUser(UserModel user) {
		currentUser = user;
	}

	public static UserModel getUser() {
		return currentUser;
	}

	public static String getRole() {
		if (currentUser == null)
			return "";
		return currentUser.getRole();
	}

	public static boolean isAdmin() {
		return "admin".equals(getRole());
	}

	public static void logout() {
		currentUser = null;
	}
}