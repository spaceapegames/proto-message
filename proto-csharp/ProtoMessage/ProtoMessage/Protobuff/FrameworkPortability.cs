using System;
using System.Globalization;
using System.Reflection;
using System.Text.RegularExpressions;

namespace Google.ProtocolBuffers
{
  internal static class FrameworkPortability
  {
    #if COMPACT_FRAMEWORK
        internal const string NewLine = "\n";

#else
    internal static readonly string NewLine = System.Environment.NewLine;
    #endif

    #if CLIENTPROFILE
        internal const RegexOptions CompiledRegexWhereAvailable = RegexOptions.Compiled;

#else
    internal const RegexOptions CompiledRegexWhereAvailable = RegexOptions.None;
    #endif

    internal static CultureInfo InvariantCulture
    { 
      get { return CultureInfo.InvariantCulture; } 
    }

    internal static double Int64ToDouble(long value)
    {
#if CLIENTPROFILE
            return BitConverter.Int64BitsToDouble(value);
#else
      double[] arresult = new double[1];
      Buffer.BlockCopy(new[] { value }, 0, arresult, 0, 8);
      return arresult[0];
#endif
    }

    internal static long DoubleToInt64(double value)
    {
#if CLIENTPROFILE
            return BitConverter.DoubleToInt64Bits(value);
#else
      long[] arresult = new long[1];
      Buffer.BlockCopy(new[] { value }, 0, arresult, 0, 8);
      return arresult[0];
#endif
    }

    internal static bool TryParseInt32(string text, out int number)
    {
      return TryParseInt32(text, NumberStyles.Any, InvariantCulture, out number);
    }

    internal static bool TryParseInt32(string text, NumberStyles style, IFormatProvider format, out int number)
    {
#if COMPACT_FRAMEWORK
                try 
                {
                    number = int.Parse(text, style, format); 
                    return true;
                }
                catch 
                {
                    number = 0;
                    return false; 
                }
#else
      return int.TryParse(text, style, format, out number);
#endif
    }
  }
}

