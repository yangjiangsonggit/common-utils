rf 拣货
=========
 ##1. 调用接口 /picking/batchPick
 		传入参数: taskNo  		任务号(拣货任务)
 				 containerNo 	容器号(拣货容器)


 		流程:
 				//开始拣货
 				(1)startPicking()
 					//验证任务,容器,登录人(拣货区域权限等)
 					startPickingValidate()

 					startPicking()

 						//更新明细
 						updateTaskDetailStartPicking()

 						//如果拣货任务状态为未开始,则修改为拣货中
 						updateTaskWhenStatusNew()

 						//更新出库单
 						updateOutBoundOrder()

 							//更新出库单明细,根据拣货任务明细修改出库单明细状态为301(拣货中)
 							updateOutBoundOrderDetail()
 							//检查拣货任务相关的出库单的状态不能小于300或大于750
 							//记录出库单日志

 						//更新容器为占用

 				//返回页面需要的数据
 				(2)getPickViewResult()
 					//检查taskDetailDOList,看拣货任务是否完成,size=0,finish=true
 					//如果拣货任务未完成, 返回下两个明细任务
 					//根据shipper_no和sku查询item,并跟展示信息到明细任务
 					//查找未拣数量和任务明细数,主要是未拣库位数量和未拣sku数量
 					getTaskDetailUnpickCountAndNum()
 					//返回信息有:
 					barcodeList
 					disPlayNum1
 					unPickLocationCount
 					unPickNum
 					nextLocation
 					urgentTask
 					validateLocation
 					validateItem
 					validateQuantity
 					lessPickup	//是否短拣是从拣货任务明细查出库单号,出库单查出库单类型,出库单类型有是否支持短拣


 ##2. 调用接口 /picking/pick
 		传入参数: taskNo			任务号
 				 detailNos
 				 afterLocationNo
 				 afterSku
 				 isZonePick
 				 locationNo
 				 sku
 				 qty

 		流程:

 			//拣货
 			pick()
 				//检查是否已经有人拣货
 				//检查任务状态,库位,sku
 				//检查拣货数量
 				picking()
 					//根据任务明细获取订单类型
 					//判断是否允许短捡
 					//判断是否允许部分出库
 					//判断是否允许重新分配
 					//按盘点差异规则反序拣货
 					sortTaskDetails()
 				//判断是否短捡
 					//是
 					//重新分配
 					reassign()
 						//货箱明细号
 						WaveOrderShippingContainerDO = waveOrderShippingContainerService.findByNo
 						getDistributionOrderDetailMO()
 						//拣货中遇到短拣情况，重新分配库位范围、库存的算法
 						
 						//区分拣货任务中短拣、正常订单的库位分配算法。故需要给接口传入是什么类型的拣货
 						//类型，这个类型关联到分配库位范围上，方便查询。 算法：
					    //在分配库位范围表上加一个区分拣货任务、是正常订单还是短拣的情况标识，因为在库
					    //位分配接口的时候需要用到。短拣的情况的sql需要进行代码拼接。 
					    //短拣：sql：正常sql+ 区段=传入区段 and 库位标识>=记录的当前库位标识
					    //这个sql查询不到情况下就要查询 正常sql+ 区段=传入区段 and 
					    //库位标识<=记录的当前库位标识
					    //订单的sql：配置在界面上的sql。 考虑到短拣的情况，模式是不用修改表结构，而
					    //是专门提供了另外一个分配库位的接口，专门为短拣分配库位，参数就需要传入比这个
					    //多。
					    List<InventoryDO> = distributionLocationRangeForShortPicking()
					    	//先判断出库单明细中是否有拣货库位还需要验证批次
					    	checkAndGetPickingLocationInOutboundOrderDetail()
					    	//每次分配的时候，都要先检查和获取一下是否有picking_location，如果明细
					    	//配置了拣货库位，那么他优先级最高，就强制返回拣货库位了， 
					    	//不用走规则和动态指派相关的流程了
						    //。也不需要分配规则标识了。所有外部接口调用分配和规则标识获取接口之前的
						    //时候，都需要调用这个，如果有值就直接用这个，没有值就用规则的。
						    //调用在getDistributionRuleNoByOutboundOrderDetail和distribution
						    //LocationRangeForNormal
						    //、distributionLocationRangeForShortPicking之前。
						    	String pickingLocation = findPickingLocationInOutboundOrderDetail(orderNo,orderLineNo)

						    //如果检查到拣货库位不为空，那么就用这个拣货库位查询相关库存信息
						    //根据查询出来的拣货库位去查询库存里面关联的信息，需要根据订单号、订单行号获取sku、
						    //shipperNo、批属性等信息.先根据订单号、
						    //订单行号确定出库单明细然后从明细中获取sku、shipperNo、批属性等信息
						    List<InventoryDO> = getInventorsByPickingLocation(DistributionOrderDetailMO)
						    //库存信息集合，一个明细可能是多个库存信息
						    //查询一条定位规则的所有明细
						    List<DistributionRuleDetailShowMO> = findDistributionRuleDetails(String distributionRuleNo);
						    	//先通過緩存查詢定位規則明細
						    	//如果查询缓存中是空，走DB查,然后进行缓存的添加
						    //循环获取分配规则明细关联的分配库位范围标识
						    //查询一条出库分配库位范围信息
						    DistributionLocationRangeDO = findByNo(String distributionLocationRangeNo);
						    String locationSql = distributionLocationRangeDO.getCustomSql();

						    //判断参数中当前区段标识是否为空，如果不为空则按照同区段进行分配库位。如果为空则全仓库进行分配
			                // 1、重新分配，则继续判断是同区段分配还是全仓分配,判断规则就是根据任务中的当前区段标识是否为空，如果为空，则进行全仓分配，如果有值则进行当前区段分配
			                // （1.1）
			                // 同区段分配，则按照比当前库位拣货顺序大的库位依次分配，再按照比该库位拣货顺序小的方向逆向分配，也未找到，隐藏该页跳转到下一库位拣货页面，最后库位则返回批量拣选页；如果找到重新分配的库位，则将此拣货页面按照拣货顺序排列到接下来的拣货中。
			                // （1.2） 全仓分配，则按照同区段的规则去找，若没有找到，则到全仓的库位去找。

			                //判断curZoneNo是否为空
			                	//是,全仓分配
			                	inventorys.addAll(distributionAllZoneLocationRangeForShortPicking(sqlnew, curLocationNo, orderSize))
			                	//否,同区段分配
			                	inventorys.addAll(distributionSameZoneLocationRangeForShortPicking(sqlnew, curZoneNo, curLocationNo, orderSize))
			                //计算可用量,并且返回
			                calculateBatchCanDistributionQty(inventorys);

			                //计算查询出来的库存可分配的可用量，可用量=sum(on_hand_qty-allocated_qty) 暂时未用到，不过还是可以用的

			            //可分配库存
			            allQty = List<InventoryDO>的和
			            //为该条分配明细分配库存，并且返回未分配的数量
			            unAllocateQty = allocateTheInventory(taskDO, taskDetailDO, inventory, oldContainerDO, unAllocateQty, 1)
			            	//如果超过递归限制次数认为分配到足够的库存，则将剩余未分配的库存数返回
			            	//增加库存分配量
			            	adjustAllocatedQty(InventoryKey inventoryKey, Double adjustQty, String userName)
			            	//插入货箱明细

			            	//生成任务明细

			            	//更新失败，说明该条库存已被其他线程分配，且可用量小于分配预期量，重新获取该库存，检查是否
			            	//还有可用量
			 				//如果有可用量，将可用量分配到该明细，如果可用量为0，则返回，继续分配下一条库存
			 				allocateTheInventory()

			            //unAllocateQty = 0 时结束

			            //分配成功  或者  没有分配成功且允许部分出库
			            //更新num1数量,货箱订单明细
			            waveOrderShippingContainerService.update(waveOrderShippingContainerDO);

			            //增加重新分配次数
			            TaskDO updateTask = new TaskDO();
						updateTask.setTaskNo(taskDO.getTaskNo());
						updateTask.setReassignCount(taskDO.getReassignCount() == null ? 1 : taskDO.getReassignCount() + 1);
						update(updateTask)
					//不短拣
					nextTwoDetails = normalPicking(taskDO, taskDetailDO, pickQty, locationNo, afterLocationNo, afterSku, isLast, operator);
						//更新任务明细
						//更新货箱明细
						//扣减库存
						doCompletePicking()
							//isLastTaskDetail
							nextTwoDetails = findNextDetailByLocation(taskDO.getTaskNo(), afterLocationNo, afterSku);
							//如果nextTwoDetails为空,则更新拣货任务明细和拣货任务为完成
							//更新拣货时间和拣货人(锁定任务)
							updateTaskDetailStartPicking()
							//更新订单
							updateOutBoundOrder(TaskDO taskDO, TaskDetailDO taskDetailDO, String operator)

				//同批量拣货返回值
				getPickViewResult()
				






















