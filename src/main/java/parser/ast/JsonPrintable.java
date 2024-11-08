package parser.ast;

import java.lang.reflect.Field;

public abstract class JsonPrintable {
    public String toString(int depth) {
        String tab = String.format("%" + (2 * (depth + 1)) + "s", " ");
        String tabLessDepth = depth == 0 ? "" : String.format("%" + (2 * depth) + "s", " ");
        Class<?> classMirror = this.getClass();

        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append(classMirror.getSimpleName()).append(": {\n");

        while (classMirror != null) {
            Field[] fields = classMirror.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    Object value = field.get(this);
                    jsonBuilder.append(tab).append(field.getName()).append(": ");
                    if (value == null) {
                        jsonBuilder.append("null");
                    } else {
                        if (value instanceof Iterable<?>) {
                            jsonBuilder.append("[");
                            Iterable<?> iterable = (Iterable<?>) value;
                            boolean nonEmpty = false;
                            for (Object element : iterable) {
                                if (element instanceof JsonPrintable) {
                                    jsonBuilder.append(((JsonPrintable) element).toString(depth + 1));
                                } else if (element == null){
                                    jsonBuilder.append("null");
                                } else {
                                    jsonBuilder.append(element.toString());
                                }
                                jsonBuilder.append(", ");
                                nonEmpty = true;
                            }

                            if (nonEmpty) jsonBuilder.delete(jsonBuilder.length() - 2, jsonBuilder.length());
                            jsonBuilder.append("]");
                        } else if (value instanceof JsonPrintable) {
                            jsonBuilder.append(((JsonPrintable) value).toString(depth + 1));
                        } else {
                            jsonBuilder.append(value.toString());
                        }
                    }

                    jsonBuilder.append("\n");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            // Move to the superclass for inherited fields
            classMirror = classMirror.getSuperclass();
        }

        jsonBuilder.append(tabLessDepth).append("}");
        return jsonBuilder.toString();
    }

    @Override
    public String toString() {
        return toString(0);
    }
}