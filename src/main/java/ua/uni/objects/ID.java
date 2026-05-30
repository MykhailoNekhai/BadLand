    package ua.uni.objects;


    import java.util.Objects;

    public class ID {
        private final int number;

        public ID(int number) {
            validate (number);
            this.number = number;
        }

        public static void validate(int checkNumber) {
            if (checkNumber <= 0) {
                throw new IllegalArgumentException("ID is less than 0");
            }
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ID id = (ID) o;
            return this.number == id.number;
        }
        @Override
        public int hashCode() {
            return Objects.hash(number);
        }
        @Override
        public String toString() {
            return String.valueOf(number);
        }
        public int getID() {
            return number;
        }
    }