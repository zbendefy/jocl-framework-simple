package clframework.common;

import static org.jocl.CL.CL_CONTEXT_PLATFORM;
import static org.jocl.CL.clCreateCommandQueue;
import static org.jocl.CL.clCreateContext;
import static org.jocl.CL.clFinish;
import static org.jocl.CL.clReleaseCommandQueue;
import static org.jocl.CL.clReleaseContext;

import org.jocl.cl_command_queue;
import org.jocl.cl_context;
import org.jocl.cl_context_properties;
import org.jocl.cl_device_id;

public class CLContext {
	cl_context context;
	cl_command_queue commandQueue;
	CLDevice clDevice;

	/**
	 * Returns the opencl context
	 * @return
	 */
	public cl_context getContext() {
		return context;
	}

	/**
	 * Returns the command queue
	 * @return
	 */
	public cl_command_queue getCommandQueue() {
		return commandQueue;
	}

	/**
	 * Returns the CLDevice instance of the context
	 * @return
	 */
	public CLDevice getClDevice() {
		return clDevice;
	}

	/**
	 * Creates a new OpenCL context using the specified OpenCL device
	 * @param clDevice
	 */
	public CLContext(CLDevice clDevice) {
		super();

		this.clDevice = clDevice;

		cl_context_properties contextProperties = new cl_context_properties();
		contextProperties.addProperty(CL_CONTEXT_PLATFORM,
				clDevice.getClPlatform());

		// Create a context for the selected device
		context = clCreateContext(contextProperties, 1,
				new cl_device_id[] { clDevice.getClDevice() }, null, null, null);

		// Create a command-queue for the selected device
		commandQueue = clCreateCommandQueue(context, clDevice.getClDevice(), 0,
				null);
	}
	
	/**
	 * This function will block until all commands finish in the command queue.
	 */
	public void WaitUntilCommandsFinish()
	{
		clFinish(commandQueue);
	}

	/**
	 * Releases resources used by the context
	 */
	public void delete() {
		if (commandQueue != null) {
			clReleaseCommandQueue(commandQueue);
			commandQueue = null;
		}

		if (context != null) {
			clReleaseContext(context);
			context = null;
		}

	}

}
