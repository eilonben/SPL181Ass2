package bgu.spl.a2.sim;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class JsonComputer {
    public String Type;
    @SerializedName("Sig Fail")
    public String sigFail;
    @SerializedName("Sig Success")
    public String sigSuccess;

    public String getType() {
        return Type;
    }

    public long getSigFail() {
        return Long.parseLong(sigFail);
    }

    public long getSigSuccess() {
        return Long.parseLong(sigSuccess);
    }
}
