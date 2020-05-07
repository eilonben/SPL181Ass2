package bgu.spl.a2.sim;

import com.google.gson.annotations.SerializedName;

public class UniversitySystem {
    public int threads;
    public JsonComputer[] Computers;
    @SerializedName("Phase 1")
    public ActionJson[] Phase_1;
    @SerializedName("Phase 2")
    public ActionJson[] Phase_2;
    @SerializedName("Phase 3")
    public ActionJson[] Phase_3;

    public int getThreads() {
        return threads;
    }

    public JsonComputer[] getComputers() {
        return Computers;
    }

    public ActionJson[] getPhase_1() {
        return Phase_1;
    }

    public ActionJson[] getPhase_2() {
        return Phase_2;
    }

    public ActionJson[] getPhase_3() {
        return Phase_3;
    }
}
