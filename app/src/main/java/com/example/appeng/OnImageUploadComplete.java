package com.example.appeng;

public interface OnImageUploadComplete {
    void onSuccess(String imageUrl);  // Khi tải lên thành công
    void onFailure(Exception e);     // Khi gặp lỗi
}
