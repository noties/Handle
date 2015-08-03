package ru.noties.handle.sample;

/**
 * Created by Dimitry Ivanov on 21.07.2015.
 */
public class NotParcelableObject {

    private String s;
    private int i;
    private long l;
    private NotParcelableObject o;

    public NotParcelableObject getO() {
        return o;
    }

    public NotParcelableObject setO(NotParcelableObject o) {
        this.o = o;
        return this;
    }

    public String getS() {
        return s;
    }

    public NotParcelableObject setS(String s) {
        this.s = s;
        return this;
    }

    public int getI() {
        return i;
    }

    public NotParcelableObject setI(int i) {
        this.i = i;
        return this;
    }

    public long getL() {
        return l;
    }

    public NotParcelableObject setL(long l) {
        this.l = l;
        return this;
    }

    @Override
    public boolean equals(Object o1) {
        if (this == o1) return true;
        if (o1 == null || getClass() != o1.getClass()) return false;

        NotParcelableObject object = (NotParcelableObject) o1;

        if (i != object.i) return false;
        if (l != object.l) return false;
        if (s != null ? !s.equals(object.s) : object.s != null) return false;
        return !(o != null ? !o.equals(object.o) : object.o != null);

    }

    @Override
    public int hashCode() {
        int result = s != null ? s.hashCode() : 0;
        result = 31 * result + i;
        result = 31 * result + (int) (l ^ (l >>> 32));
        result = 31 * result + (o != null ? o.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "NotParcelableObject{" +
                "s='" + s + '\'' +
                ", i=" + i +
                ", l=" + l +
                ", o=" + o +
                '}';
    }
}
