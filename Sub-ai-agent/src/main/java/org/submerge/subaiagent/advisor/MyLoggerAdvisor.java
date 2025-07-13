package org.submerge.subaiagent.advisor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.AdvisedRequest;
import org.springframework.ai.chat.client.advisor.api.AdvisedResponse;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAroundAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAroundAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAroundAdvisorChain;
import org.springframework.ai.chat.model.MessageAggregator;
import reactor.core.publisher.Flux;

@Slf4j
public class MyLoggerAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {

	public String getName() {
		return this.getClass().getSimpleName();
	}

	public int getOrder() {
		return 0;
	}

	private AdvisedRequest before(AdvisedRequest request) {
		// 打印请求中的用户文本
		log.info("request: {}",request.userText());
		return request;
	}

	private void observeAfter(AdvisedResponse advisedResponse) {
		log.info("response: {}",advisedResponse.response().getResult().getOutput().getText());
	}

	public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
		advisedRequest = this.before(advisedRequest);
		AdvisedResponse advisedResponse = chain.nextAroundCall(advisedRequest);
		this.observeAfter(advisedResponse);
		return advisedResponse;
	}

	public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
		advisedRequest = this.before(advisedRequest);
		Flux<AdvisedResponse> advisedResponses = chain.nextAroundStream(advisedRequest);
		return (new MessageAggregator()).aggregateAdvisedResponse(advisedResponses, this::observeAfter);
	}
}
