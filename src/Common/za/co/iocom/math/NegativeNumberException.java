package za.co.iocom.math;

/**
	This exception is thrown when a positive or zero value has been
	expected, but a negative value was found.
*/
class NegativeNumberException extends Exception
{
	int num;

	/**Constructs a new NegativeNumberException*/
	NegativeNumberException()
	{
		super();
		num = 0;
	}

	/**
		Constructs a new NegativeNumberException
		@param n the negative number that caused the exception
	*/
	NegativeNumberException(int n)
	{
		super();
		num = n;
	}




}
