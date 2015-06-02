__kernel void vecadd(__global const float *input_a, __global const float *input_b, __global float *output) 
{ 
	const uint threadId = get_global_id(0);
	
	output[threadId] = input_a[threadId] + input_b[threadId];
}