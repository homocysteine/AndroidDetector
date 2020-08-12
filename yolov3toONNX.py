import cv2
#必须要用绝对地址，相对地址会有问题
net = cv2.dnn.readNetFromONNX(r'C:\Users\14841\AndroidStudioProjects\AndroidDetector\yolov3-myyolov3_99_0.96_warehouse.onnx')
#print(net.getLayerNames())
print(net.getUnconnectedOutLayersNames())