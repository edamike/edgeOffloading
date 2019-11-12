package at.ac.tuwien.offloading.livelab;

public class LiveLab {

    private String uid;
    private String towerId;

    public LiveLab(String uid, String towerId) {
        this.uid = uid;
        this.towerId = towerId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTowerId() {
        return towerId;
    }

    public void setTowerId(String towerId) {
        this.towerId = towerId;
    }
}
