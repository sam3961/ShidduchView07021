package shiddush.view.com.mmvsd.model.questions;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Data{

	@SerializedName("question")
	private String question;

	@SerializedName("answers")
	private List<AnswersItem> answers;

	@SerializedName("question_id")
	private String questionId;

	@SerializedName("timeslots")
	private TimeSlots timeSlots;

	@SerializedName("is_main")
	private boolean is_main;

	public String getQuestion(){
		return question;
	}

	public List<AnswersItem> getAnswers(){
		return answers;
	}

	public String getQuestionId(){
		return questionId;
	}

	public TimeSlots getTimeSlots() {
		return timeSlots;
	}

	public boolean IsMain() {
		return is_main;
	}
}