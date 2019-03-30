package com.huarui.dao;

import com.huarui.model.XcOrders;
import com.huarui.model.XcTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface XcOrdersRepository extends JpaRepository<XcOrders, String> {


}
