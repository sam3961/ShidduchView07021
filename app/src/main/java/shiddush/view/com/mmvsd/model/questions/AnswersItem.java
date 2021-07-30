package shiddush.view.com.mmvsd.model.questions;

import com.google.gson.annotations.SerializedName;

public class AnswersItem{

	@SerializedName("answer")
	private String answer;

	@SerializedName("_id")
	private String id;

	public String getAnswer(){
		return answer;
	}

	public String getId(){
		return id;
	}
}