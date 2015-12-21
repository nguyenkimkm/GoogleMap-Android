# GoogleMap-Android
Ứng dụng tìm kiếm các vị trí xung quanh trên GoogleMap.

Em chưa xử lý được các vấn đề tối ưu khi mạng chập chờn nên ở đây em xin ghi phần mô tả và chạy thử chương trình.

Sử dụng API của Foursquare để lấy các vị trí xung quanh một vị trí cụ thể xác định bởi kinh độ và vĩ độ.
Chuỗi API sử dụng:"https://api.foursquare.com/v2/venues/search?client_id=C3ML2TDLI315P4WG2C0GSIHTLX3NTEG005P5UUAAKVODKB0R" +
                                            "&client_secret=LKACZPLHNPQ5TGN2HL1SIVAMU0Z2JGG0RMMDQV3NW3ARMTBE" +
                                            "&limit=50" +
                                            "&intent=browse" +
                                            "$radius=800" +
                                            "&ll="+String.valueOf(obj.getmLat())+","+String.valueOf(obj.getmLng()) +
                                            "&v="+currentTime();

Ứng dụng chạy thử trên máy ảo của Genymotion - Sony Xperia Z-4.2.2-API 17. Màn hình chính được chia làm 2 phần:
 - Phần trên: hiển thị vị trí hiện tại trên GoogleMap.
 - Phần dưới: hiển thị danh sách các vị trí xung quanh vị trí hiện tại với các thông tin: tên vị trí, địa chỉ, khoảng cách.

Sử dụng 2 icon xử lý sự kiện clik:
 - Icon home (góc phải dưới): Hiển thị đầy đủ màn hình bản đồ GoogleMap và đánh dấu trên bản đồ một số địa điểm xung quanh.
 - Icon search (góc trái trên): Hiển thị lại danh sách các vị trí xung quanh.
Chạy thử chương trình với vị trí: 10.764102, 106.656254 (công ty VNG). Bật GPS của máy ảo để lấy tọa độ hoặc lấy tọa độ trên GoogleMap để đưa vào máy ảo. (giới hạn số vị trí tìm được là limit=50, radius=800).

Một số vị trí tìm được:
 + The Flemington - 184 Lê Đại Hành, P.15, Q11 - Khoảng cách 8 m
 + VNG Corporation - 182 Lê Đại Hành - Khoảng cách 19 m
 + Nhà Hàng Lẩu Cá Đại Thống - Ground Floor 182 Lê Đại Hành - Khoảng cách 18 m
 + Báo điện tử Zing.vn - Tần 15, Tòa Nhà Flemington - Khoảng cách 20 m.
 + ....
