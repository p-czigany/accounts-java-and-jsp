package com.xcite.javatest.action.web;

import com.xcite.core.interfaces.IWebAction;
import com.xcite.core.servlet.ParameterMap;
import com.xcite.core.servlet.ProcessResult;
import com.xcite.core.utils.DataBase;
import com.xcite.core.utils.SqlQuery;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EditUser extends IWebAction {
    @Override
    public ProcessResult processRequest(ParameterMap parameterMap) throws Throwable {
        ProcessResult result = ProcessResult.createProcessDispatchResult("editUser");

        String userId = parameterMap.get("id");
        System.out.println("I got the id! It's: " + userId);

        SqlQuery query = new SqlQuery("xjfw.account");
        query.where.add("id", Integer.valueOf(userId));
        List<Map<String, Object>> userDataList = DataBase.select(query);

        System.out.println("I have the data retrieved from DB! It's: " + userDataList);

        Map<String, Object> userData = userDataList.get(0);
//        Optional<Map<String, Object>> userData = userDataList.stream().filter(user -> Integer.valueOf(userId).equals(user.get("id"))).findFirst();
        System.out.println("I have the user data! It's: " + userData);

        result.addData("userData", userData);

        return result;
    }

    @Override
    public String getAuthenticaionClassname() {
        return null;
    }
}
