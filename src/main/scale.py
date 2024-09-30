import json
import numpy as np

def scale_transform(data, time_scale_map):
    # 提取时间和变换矩阵
    times = data['time']
    transforms = np.array(data['transform'])

    # 创建一个新的 transform 数组
    new_transforms = transforms.copy()

    for time, scale in time_scale_map.items():
        if time in times:
            index = times.index(time)
            # 对指定时间点进行缩放
            new_transforms[index, :3] *= scale  # 只缩放前3个元素 (x, y, z)

    # 进行插值处理
    for i in range(len(times)):
        if times[i] not in time_scale_map.keys():
            # 找到上一个和下一个时间点
            previous_index = i - 1
            next_index = i + 1

            while previous_index >= 0 and times[previous_index] in time_scale_map.keys():
                previous_index -= 1
            while next_index < len(times) and times[next_index] in time_scale_map.keys():
                next_index += 1

            if previous_index >= 0 and next_index < len(times):
                # 线性插值
                t0 = times[previous_index]
                t1 = times[next_index]
                t = times[i]

                scale0 = time_scale_map[t0]
                scale1 = time_scale_map[t1]

                # 插值计算
                alpha = (t - t0) / (t1 - t0)
                interpolated_scale = scale0 + alpha * (scale1 - scale0)

                # 应用插值结果
                new_transforms[i, :3] *= interpolated_scale

    # 输出修改后的 JSON
    return {
        "name": data['name'],
        "time": times,
        "transform": new_transforms.tolist()
    }

# 示例输入
input_json = {
    "name": "Tool_R",
    "time": [0.0, 0.0417, 0.0833, 0.125, 0.1667, 0.2083, 0.25, 0.2917, 0.3333, 0.375, 0.4167, 0.4583, 0.5, 0.5417, 0.5833, 0.625],
    "transform": [
        [0.982205, -0.169419, 0.098365, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1],
        # 其他行...
    ]
}

# 时间点和缩放值
time_scale_map = {
    0.125: 2.0,
    0.5: 1.5
}

# 处理
modified_json = scale_transform(input_json, time_scale_map)

# 输出结果
print(json.dumps(modified_json, indent=4))
