package Util;

public interface WindowGetPasswordListener {
    void onPositiveResult(SecurePasswordContainer securePasswordContainer);
    void onNegativeResult(String messageNegativeResult);
}
