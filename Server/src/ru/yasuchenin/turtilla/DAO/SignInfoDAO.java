package ru.yasuchenin.turtilla.DAO;

import java.util.*;
import ru.yasuchenin.turtilla.*;

public interface SignInfoDAO {
	void addSign(SignInfo signInfo);
	List<SignInfo> listSigns();
	void updateSign(SignInfo signInfo);
}
