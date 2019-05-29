##ruleService-publishRule
//发布规则前, 先进行返券审核
        //step.1 获取所有准备发布的规则的 返券方案ID card_id
		//获取当前规则 主站表的 方案ID
		//如果主站表没有方案ID, 则审核草稿表方案ID
		//如果主站表有方案ID, 则取草稿表多出来的方案ID 来进行审核操作
		//setp.3 调用现金券接口, 审核通过这一批方案
		如果是跨时满返规则：更新（新增、修改）规则执行任务
		删除正式规则
		把规则从草稿移动到正式
		删除正式规则item
		把规则从草稿移动到正式item

##publishProcessService-notifyRulePublish

		test 
		//增加逻辑, 如果drl测试失败, 则将当前发布的所有规则的主站数据置为删除状态, 草稿站数据置为草稿状态
		notifyEngine
			reload

				// 重新加载规则
				initLhsCache
					EngineCache
				//知识库初始化完成后进行预计算, 再版本切换
				// 显示销毁无用的KieModule,否则KieModule不会被回收
			notifyCacheUpdateThread




#batchPublish
	
	ruleService-publishRule
	publishProcessService-notifyRulePublish
	sendRuleMsgToCenter-send
	
