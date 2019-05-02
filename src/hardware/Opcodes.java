package hardware;



public interface Opcodes {

	
	public static final String[][] opcodes_two_byte_with_prefix = new String[][] 
			{{"Extension on 0x0","Extension&&Secondary on 0x1","LAR","LSL","null","SYSCALL","CLTS","SYSRET","INVD","WBINVD","null",
				"UD2","null","NOP","null","null","MOVUPS","MOVUPS","MOVLPS","MOVLPS","UNPCKLPS",
				"UNPCKHPS","MOVHPS","MOVHPS","HINT_NOP","HINT_NOP","HINT_NOP","HINT_NOP","HINT_NOP","HINT_NOP","HINT_NOP",
				"HINT_NOP","MOV","MOV","MOV","MOV","MOV","null","MOV","null","MOVAPS",
				"MOVAPS","CVTPI2PS","MOVNTPS","CVTTPS2PI","CVTPS2PI","UCOMISS","COMISS","WRMSR","RDTSC","RDMSR",
				"RDPMC","SYSENTER","SYSEXIT","null","GETSEC","Secondary opcode on 0x38","null","null","null","null",
				"null","null","null","CMOVO","CMOVNO","CMOVC","CMOVNC","CMOVE","CMOVNE","CMOVNA",
				"CMOVA","CMOVS","CMOVNS","CMOVPE","CMOVPO","CMOVNGE","CMOVGE","CMOVNG","CMOVG","MOVMSKPS",
				"SQRTPS","RSQRTPS","RCPPS","ANDPS","ANDNPS","ORPS","XORPS","ADDPS","MULPS","CVTPS2PD",
				"CVTDQ2PS","SUBPS","MINPS","DIVPS","MAXPS","PUNPCKLBW","PUNPCKLWD","PUNPCKLDQ","PACKSSWB","PCMPGTB",
				"PCMPGTW","PCMPGTD","PACKUSWB","PUNPCKHBW","PUNPCKHWD","PUNPCKHDQ","PACKSSDW","null","null","MOVQ",
				"MOVQ","PSHUFW","Extension on 0x71","Extension on 0x72","Extension on 0x73","PCMPEQB","PCMPEQW","PCMPEQD","EMMS","VMREAD",
				"VMWRITE","null","null","null","null","MOVQ","MOVQ","JO","JNO","JC",
				"JNC","JE","JNE","JNA","JA","JS","JNS","JPE","JPO","JNGE",
				"JGE","JNG","JG","Extension on 0x90","Extension on 0x91","Extension on 0x92","Extension on 0x93","Extension on 0x94","Extension on 0x95","Extension on 0x96",
				"Extension on 0x97","Extension on 0x98","Extension on 0x99","Extension on 0x9a","Extension on 0x9b","Extension on 0x9c","Extension on 0x9d","Extension on 0x9e","Extension on 0x9f","PUSH",
				"POP","CPUID","BT","SHLD","SHLD","null","null","PUSH","POP","RSM",
				"BTS","SHRD","SHRD","Extension on 0xae","IMUL","CMPXCHG","CMPXCHG","LSS","BTR","LFS",
				"LGS","MOVZX","MOVZX","JMPE","UD","Extension on 0xba","BTC","BSF","BSR","MOVSX",
				"MOVSX","XADD","XADD","CMPPS","MOVNTI","PINSRW","PEXTRW","SHUFPS","Extension on 0xc7","BSWAP",
				"null","null","null","null","null","null","null","null","PSRLW","PSRLD",
				"PSRLQ","PADDQ","PMULLW","null","PMOVMSKB","PSUBUSB","PSUBUSW","PMINUB","PAND","PADDUSB",
				"PADDUSW","PMAXUB","PANDN","PAVGB","PSRAW","PSRAD","PAVGW","PMULHUW","PMULHW","null",
				"MOVNTQ","PSUBSB","PSUBSW","PMINSW","POR","PADDSB","PADDSW","PMAXSW","PXOR","null",
				"PSLLW","PSLLD","PSLLQ","PMULUDQ","PMADDWD","PSADBW","MASKMOVQ","PSUBB","PSUBW","PSUBD",
				"PSUBQ","PADDB","PADDW","PADDD","null"}

				,{"null","null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","MOVUPD","MOVUPD","MOVLPD","MOVLPD","UNPCKLPD",
				"UNPCKHPD","MOVHPD","MOVHPD","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","MOVAPD",
				"MOVAPD","CVTPI2PD","MOVNTPD","CVTTPD2PI","CVTPD2PI","UCOMISD","COMISD","null","null","null",
				"null","null","null","null","null","Secondary opcode on 0xf0","null","Secondary opcode on 0x63","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","MOVMSKPD",
				"SQRTPD","null","null","ANDPD","ANDNPD","ORPD","XORPD","ADDPD","MULPD","CVTPD2PS",
				"CVTPS2DQ","SUBPD","MINPD","DIVPD","MAXPD","PUNPCKLBW","PUNPCKLWD","PUNPCKLDQ","PACKSSWB","PCMPGTB",
				"PCMPGTW","PCMPGTD","PACKUSWB","PUNPCKHBW","PUNPCKHWD","PUNPCKHDQ","PACKSSDW","PUNPCKLQDQ","PUNPCKHQDQ","MOVQ",
				"MOVDQA","PSHUFD","Extension on 0x71","Extension on 0x72","Extension on 0x73","PCMPEQB","PCMPEQW","PCMPEQD","null","null",
				"null","null","null","HADDPD","HSUBPD","MOVQ","MOVDQA","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","CMPPD","null","PINSRW","PEXTRW","SHUFPD","Extension on 0xc7","null",
				"null","null","null","null","null","null","null","ADDSUBPD","PSRLW","PSRLD",
				"PSRLQ","PADDQ","PMULLW","MOVQ","PMOVMSKB","PSUBUSB","PSUBUSW","PMINUB","PAND","PADDUSB",
				"PADDUSW","PMAXUB","PANDN","PAVGB","PSRAW","PSRAD","PAVGW","PMULHUW","PMULHW","CVTTPD2DQ",
				"MOVNTDQ","PSUBSB","PSUBSW","PMINSW","POR","PADDSB","PADDSW","PMAXSW","PXOR","null",
				"PSLLW","PSLLD","PSLLQ","PMULUDQ","PMADDWD","PSADBW","MASKMOVDQU","PSUBB","PSUBW","PSUBD",
				"PSUBQ","PADDB","PADDW","PADDD","null"}

				,{"null","null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","MOVSD","MOVSD","MOVDDUP","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","CVTSI2SD","null","CVTTSD2SI","CVTSD2SI","null","null","null","null","null",
				"null","null","null","null","null","Secondary opcode on 0xf1","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"SQRTSD","null","null","null","null","null","null","ADDSD","MULSD","CVTSD2SS",
				"null","SUBSD","MINSD","DIVSD","MAXSD","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","PSHUFLW","null","null","null","null","null","null","null","null",
				"null","null","null","HADDPS","HSUBPS","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","CMPSD","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","ADDSUBPS","null","null",
				"null","null","null","MOVDQ2Q","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","CVTPD2DQ",
				"null","null","null","null","null","null","null","null","null","LDDQU",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null"}

				,{"null","null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","MOVSS","MOVSS","MOVSLDUP","null","null",
				"null","MOVSHDUP","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","CVTSI2SS","null","CVTTSS2SI","CVTSS2SI","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"SQRTSS","RSQRTSS","RCPSS","null","null","null","null","ADDSS","MULSS","CVTSS2SD",
				"CVTTPS2DQ","SUBSS","MINSS","DIVSS","MAXSS","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"MOVDQU","PSHUFHW","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","MOVQ","MOVDQU","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","POPCNT","null","null","null","null","null","null",
				"null","null","null","CMPSS","null","null","null","null","Extension on 0xc7","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","MOVQ2DQ","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","CVTDQ2PD",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null"}

	};
	
	public static final int[] opcodes_with_prefixes_two_bytes = new int[] {0x00,0x66,0xf2,0xf3};
	
	public static final String[][][] secondary_opcodes_with_prefix_and_primary_two_byte = new String[][][] {{{}}};
	
	public static final String[][][] primary_extensions_opcodes_two_byte = new String[][][] {{{}}};
	
	public static final String[][][][] secondary_extensions_opcodes_two_byte = new String[][][][] {{{{}}}};
	
	public static final boolean[][] has_sec_opcode_two_byte = {
			
		{false,true,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
		false,false,false,false,false,false,false,false,false,false,
		false,false,false,false,false,false,false,false,
		false,false,false,false,false,false,false,false,false,false,
		false,false,false,false,true,false,false,false,false,false,false,false,
		false,false,false,false,false,false,false,false,false,false,
		false,false,false,false,false,false,false,false,false,false,
		false,false,false,false,false,false,false,false,false,false,
		false,false,false,false,false,false,false,false,
		false,false,false,false,false,false,false,false,false,false,
		false,false,false,false,false,false,
		false,false,false,false,false,false,false,false,false,false,
		false,false,false,false,false,false,false,false,false,false,
		false,false,false,false,false,false,false,false,false,false,
		false,false,false,false,false,false,false,false,
		false,false,false,false,false,false,false,false,false,false,
		false,false,false,false,false,false,false,false,false,false,
		false,false,false,false,false,false,false,false,false,false,
		false,false,
		false,false,false,false,false,false,false,false,false,
		false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
		false,false,false,false},
		{false,false,false,false,false,
		false,false,false,false,
		false,false,false,false,false,false,false,true,true,false,
		false,false,false,false,false,false,false,false,
		false,false,false,false,false,false,false,false,false,false,
		false,false,false,false,false,false,false,false,false,false,
		false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
		false,false,false,false,false,false,false,false,false,false,
		false,false,false,false,false,false,false,false,false,false,
		false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
		false,false,false,false},
		{false,false,false,false,false,false,true,false,false,false,false,
		false,false,false,false,false,false,false,false,false,false,false,
		false,
		},
		{false,false,false,false,false,false,false,false,false,false,false,false,false,
		false,false,false,false,false,false,false,false,false,false,false,false,false,false
		}};
	
	public static boolean[][][] primary_has_ext_two_byte = new boolean[][][] {{{}}};
	
	public static boolean[][][] secondary_has_ext_two_byte = new boolean[][][]
	 {{{true,true,true,true,true,true,true,true,true,true},{false}},
	  {{false,false,false,false,false,false,false,false,false,false,false,
		false,false,false,false,false,false,false,false,false,false,
		false,false,false,false,false,false,false,false,false,false,
		false,false,false,false,false,false,false,false,false,false,
		false,false,false,false,false,false,false,false,false,false,
		false,false,false,false,false,false,false,false,false,false,
		false,false,false,false,false,false,false,false,false,false,
		false,false,false,false,false,false},
	   {false,false,false,false,false,false,false,false,false,false,false,
		false,false,false,false,false,false,false,false,false,false,
		false,false,false,false,false,false,false,false}},
	  {{false,false,false}}};
	
	public static boolean[][][] primary_has_register_two_byte = new boolean[][][] {{{}}};
	
	public static boolean[][][] secondary_has_register_two_byte = new boolean[][][] 
     {{{false,false,false,false,false,false,false,false,false,false},{true}},
	  {{true,true,true,true,true,true,true,true,true,true,true,
		true,true,true,true,true,true,true,true,true,true,
		true,true,true,true,true,true,true,true,true,true,
		true,true,true,true,true,true,true,true,true,true,
		true,true,true,true,true,true,true,true,true,true,
		true,true,true,true,true,true,true,true,true,true,
		true,true,true,true,true,true,true,true,true,true,
		true,true,true,true,true,true},
       {true,true,true,true,true,true,true,true,true,true,true,
		true,true,true,true,true,true,true,true,true,true,
		true,true,true,true,true,true,true,true}},
      {{true,true,true}
      
    }} ;
	
	public static final String[][] opcodes_with_prefix_one_byte = new String[][]		
	{
			{"ADD","ADD","ADD","ADD","ADD","ADD","PUSH","POP","OR","OR","OR",
			"OR","OR","OR","PUSH","POP","ADC","ADC","ADC","ADC","ADC",
			"ADC","PUSH","POP","SBB","SBB","SBB","SBB","SBB","SBB","PUSH",
			"POP","AND","AND","AND","AND","AND","AND","ES","DAA","SUB",
			"SUB","SUB","SUB","SUB","SUB","NTAKEN","DAS","XOR","XOR","XOR",
			"XOR","XOR","XOR","SS","AAA","CMP","CMP","CMP","CMP","CMP",
			"CMP","TAKEN","AAS","REX","REX.B","REX.X","REX.XB","REX.R","REX.RB","REX.RX",
			"REX.RXB","REX.W","REX.WB","REX.WX","REX.WXB","REX.WR","REX.WRB","REX.WRX","REX.WRXB","PUSH",
			"null","null","null","null","null","null","null","POP","null","null",
			"null","null","null","null","null","PUSHAD","POPAD","BOUND","MOVSXD","ALTER",
			"GS","null","null","PUSH","IMUL","PUSH","IMUL","INSB","INSD","OUTSB",
			"OUTSD","JO","JNO","JC","JNC","JE","JNE","JNA","JA","JS",
			"JNS","JPE","JPO","JNGE","JGE","JNG","JG","Extension on 0x80","Extension on 0x81","Extension on 0x82",
			"Extension on 0x83","TEST","TEST","XCHG","XCHG","MOV","MOV","MOV","MOV","MOV",
			"LEA","MOV","Extension on 0x8f","NOP","null","null","null","null","null","null",
			"null","CDQE","CQO","CALLF","WAIT","PUSHFQ","POPFQ","SAHF","LAHF","MOV",
			"MOV","MOV","MOV","MOVSB","MOVSQ","CMPSB","CMPSQ","TEST","TEST","STOSB",
			"STOSQ","LODSB","LODSQ","SCASB","SCASQ","MOV","null","null","null","null",
			"null","null","null","MOV","null","null","null","null","null","null",
			"null","Extension on 0xc0","Extension on 0xc1","RETN","RETN","LES","LDS","Extension on 0xc6","Extension on 0xc7","ENTER",
			"LEAVE","RETF","RETF","INT","INT","INTO","IRETQ","Extension on 0xd0","Extension on 0xd1","Extension on 0xd2",
			"Extension on 0xd3","Secondary opcode on 0xa","Secondary opcode on 0xa","SETALC","XLATB","Extension&&Secondary on 0xd8","Extension&&Secondary on 0xd9","Extension&&Secondary on 0xda","Extension&&Secondary on 0xdb","Extension on 0xdc",
			"Extension&&Secondary on 0xdd","Extension&&Secondary on 0xde","Extension&&Secondary on 0xdf","LOOPNE","LOOPE","LOOP","JRCXZ","IN","IN","OUT",
			"OUT","CALL","JMP","JMPF","JMP","IN","IN","OUT","OUT","LOCK",
			"ICEBP","REP","REP","HLT","CMC","Extension on 0xf6","Extension on 0xf7","CLC","STC","CLI",
			"STI","CLD","STD","Extension on 0xfe","Extension on 0xff"},
		
		       
		{"null","null","null","null","null","null","null","null","null","null","null",
			"null","null","null","null","null","null","null","null","null","null",
			"null","null","null","null","null","null","null","null","null","null",
			"null","null","null","null","null","null","null","null","null","null",
			"null","null","null","null","null","null","null","null","null","null",
			"null","null","null","null","null","null","null","null","null","null",
			"null","null","null","null","null","null","null","null","null","null",
			"null","null","null","null","null","null","null","null","null","null",
			"null","null","null","null","null","null","null","null","null","null",
			"null","null","null","null","null","null","null","null","null","null",
			"null","null","null","null","null","null","null","null","null","null",
			"null","null","null","null","null","null","null","null","null","null",
			"null","null","null","null","null","null","null","null","null","null",
			"null","null","null","null","null","null","null","null","null","null",
			"null","null","null","null","null","null","null","null","null","null",
			"null","null","null","null","null","null","null","null","null","null",
			"null","null","null","null","null","null","null","null","null","null",
			"null","null","null","null","null","null","null","null","null","null",
			"null","null","null","null","null","null","null","null","null","null",
			"null","null","null","null","null","null","null","null","null","null",
			"null","null","null","null","null","null","null","null","null","null",
			"null","null","null","null","null","null","Extension&&Secondary on 0xd9","null","Extension&&Secondary on 0xdb","null",
			"Extension on 0xdd","null","Extension&&Secondary on 0xdf","null","null","null","null","null","null","null",
			"null","null","null","null","null","null","null","null","null","null",
			"null","null","null","null","null","null","null","null","null","null",
			"null","null","null","null","null"},
		
				
			{"null","null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","PAUSE","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null","null","null","null","null","null",
				"null","null","null","null","null"}
			
	};
	public static final int[] prefix_indexes_with_codes_one_byte = new int[] {0x00,0x9B,0xF3};
	
	public static final int[][] opcodes_with_prefix_indexes_one_byte = new int[][] {{0x00},{0xD9,0xDB,0xDD,0xDF},{0x90}};
	
	public static final String[] prefix_names = new String[] {"Not a prefix","ES","NTAKEN","SS","TAKEN","REX","REX.B","REX.X","REX.XB","REX.R",
			"REX.RB","REX.RX","REX.RXB","REX.W","REX.WB","REX.WX",
			"REX.WXB","REX.WR","REX.WRB","REX.WRX","REX.WRXB","ALTER","GS","<Operand size override>","<Address size override>","WAIT","LOCK","REP","REP"};
	
	public static final String[] prefix_names_variants = new String[] {};
	
	public static final String[] prefix_names_variants_indexes = new String[] {};
	
	public static final String[] prefix_names_variants_proc = new String[] {};
	
	public static final String[] proc_names = new String[] {"Default","8086","80286","80386","80486","Pentium (1)","Pentium with MMX",
			"Pentium Pro","Pentium II", "Pentium III","Pentium 4","Core","Core 2","Core i7","Itanium"};
	
	public static final String[] proc_codes = new String[] {"0","01","02","03","04","P1","PX","PP","P2","P3","P4","C1","C2","C3","IT"};
	
	public static final int[] proc_codes_int = new int[] {0,1,2,3,4,5,6,7,8,9,10,11,12,13,99};
	
	public static final int[] prefix_codes = new int[] {0x0,0x26,0x2e,0x36,0x3e,0x40,0x41,0x42,0x43,0x44,0x45,0x46,0x47,
		0x48,0x49,0x4a,0x4b,0x4c,0x4d,0x4e,0x4f,0x64,0x65,0x66,0x67,0x9b,0xf0,0xf2,0xf3};
	
	public static final int[] prefix_codes_index = new int[] {0x0,0x1,0x2,0x3,0x4,0x5,0x6,0x7,0x8,0x9,0xA,0xB,0xC,0xD,
			0xE,0xF,0x10,0x11,0x12,0x13,0x14,0x15,0x16,0x17,0x18,0x19,0x1A,0x1B,0x1C};
		
	
	public static final String[][][] extensions_one_byte = new String[][][]
		{{{"ADD","OR","ADC","SBB","AND","SUB","XOR","CMP"},
			{"ADD","OR","ADC","SBB","AND","SUB","XOR","CMP"},
			{"ADD","OR","ADC","SBB","AND","SUB","XOR","CMP"},
			{"ADD","OR","ADC","SBB","AND","SUB","XOR","CMP"},
			{"POP","null","null","null","null","null","null","null"},
			{"ROL","ROR","RCL","RCR","SAL","SHR","SHL","SAR"},
			{"ROL","ROR","RCL","RCR","SAL","SHR","SHL","SAR"},
			{"MOV","null","null","null","null","null","null","null"},
			{"MOV","null","null","null","null","null","null","null"},
			{"ROL","ROR","RCL","RCR","SAL","SHR","SHL","SAR"},
			{"ROL","ROR","RCL","RCR","SAL","SHR","SHL","SAR"},
			{"ROL","ROR","RCL","RCR","SAL","SHR","SHL","SAR"},
			{"ROL","ROR","RCL","RCR","SAL","SHR","SHL","SAR"},
			{"FADD","FMUL","FCOM","FCOMP","FSUB","FSUBR","FDIV","FDIVR"},
			{"FLD","FXCH","FST","FSTP1","FLDENV","FLDCW","FNSTENV","null"},
			{"FCMOVB","FCMOVE","FCMOVBE","FCMOVU","FISUB","FISUBR","FIDIV","FIDIVR"},
			{"FCMOVNB","FCMOVNE","FCMOVNBE","FCMOVNU","null","null","null","null"},
			{"FADD","FMUL","FCOM2","FCOMP3","FSUBR","FSUB","FDIVR","FDIV"},
			{"FFREE","FXCH4","FST","FSTP","FUCOM","FUCOMP","FNSAVE","null"},
			{"FADDP","FMULP","FCOMP5","FICOMP","FSUBRP","FSUBP","FDIVRP","FDIVP"},
			{"FFREEP","FXCH7","FSTP8","FSTP9","FBLD","null","null","null"},
			{"TEST","TEST","NOT","NEG","MUL","IMUL","DIV","IDIV"},
			{"TEST","TEST","NOT","NEG","MUL","IMUL","DIV","IDIV"},
			{"INC","DEC","null","null","null","null","null","null"},
			{"INC","DEC","CALL","CALLF","JMP","JMPF","PUSH","null"}},
			{{"null","null","null","null","null","null","FSTENV","FSTCW"},
			{"null","null","null","null","null","FUCOMI","FCOMI","FSTP"},
			{"null","null","null","null","null","null","FSAVE","FSTSW"},
			{"null","null","null","null","null","FUCOMIP","FCOMIP","FISTP"}}};
	
		//	{"FCOM","FCOMP"},{"FXCH","FNOP","FCHS","FABS","FIST","FXAM","FLD1","FLDL2T","FLDL2E"}		
			
	public static final String[][][][] extensions_one_byte_with_two_byte = new String[][][][]
	{{{{"null","null","FCOM","null","null","null","null","null"},
	   {"null","null","null","FCOMP","null","null","null","null"}}, //0xD8
	  {{"null","FXCH","null","null","null","null","null","null"},
	   {"null","null","FNOP","null","null","null","null","null"},
	   {"null","null","null","null","FCHS","null","null","null"},
	   {"null","null","null","null","FABS","null","null","null"},
	   {"null","null","null","null","FTST","null","null","null"},
	   {"null","null","null","null","FXAM","null","null","null"},
	   {"null","null","null","null","null","FLD1","null","null"},
	   {"null","null","null","null","null","FLDL2T","null","null"},
	   {"null","null","null","null","null","FLDL2E","null","null"},
	   {"null","null","null","null","null","FLDPI","null","null"},
	   {"null","null","null","null","null","FLDLG2","null","null"},
	   {"null","null","null","null","null","FLDLN2","null","null"},
	   {"null","null","null","null","null","FLDZ","null","null"},
	   {"null","null","null","null","null","null","F2XM1","null"},
	   {"null","null","null","null","null","null","FYL2X","null"},
	   {"null","null","null","null","null","null","FPTAN","null"},
	   {"null","null","null","null","null","null","FPATAN","null"},
	   {"null","null","null","null","null","null","FXTRACT","null"},
	   {"null","null","null","null","null","null","FPREM1","null"},
	   {"null","null","null","null","null","null","FDECSTP","null"},
	   {"null","null","null","null","null","null","FINCSTP","null"},
	   {"null","null","null","null","null","null","null","FPREM"},
	   {"null","null","null","null","null","null","null","FYL2XP1"},
	   {"null","null","null","null","null","null","null","FSQRT"},
	   {"null","null","null","null","null","null","null","FSINCOS"},
	   {"null","null","null","null","null","null","null","FRNDINT"},
	   {"null","null","null","null","null","null","null","FSCALE"},
	   {"null","null","null","null","null","null","null","FSIN"},
	   {"null","null","null","null","null","null","null","FCOS"}}, //0xD9
	  {{"null","null","null","null","null","FUCOMPP","null","null"}}, //0xDA
	  {{"null","null","null","null","FNENI","null","null","null"},
	  {"null","null","null","null","FNDISI","null","null","null"},
	  {"null","null","null","null","FNINIT","null","null","null"},
	  {"null","null","null","null","FNSETPM","null","null","null"}}, //0xDB
	  {{"null","null","null","null","FUCOM","null","null","null"},
	   {"null","null","null","null","null","FUCOMP","null","null"}}, //0xDD
	  {{"FADDP","null","null","null","null","null","null","null"},
	   {"null","FMULP","null","null","null","null","null","null"},
	   {"null","null","null","FCOMPP","null","null","null","null"},
	   {"null","null","null","null","FSUBRP","null","null","null"},
	   {"null","null","null","null","null","FSUBP","null","null"},
	   {"null","null","null","null","null","null","FDIVRP","null"},
	   {"null","null","null","null","null","null","null","FDIVP"}}, //0xDE
      {{"null","null","null","null","FNSTSW","null","null","null"}}},//0xDF
	 
	{{{"null","null","null","null","FCLEX","null","null","null"},
	   {"null","null","null","null","FINIT","null","null","null"}}, //0xDB
	  {{"null","null","null","null","FSTSW","null","null","null"}	//0xDE	
	}}};
	
	public static final String[][][] secondary_one_byte = new String[][][] 
	{{{"AAM","AMX"},
	{"AAD","ADX"},
	{"Extension on secondary opcode 0xd1","Extension on secondary opcode 0xd9"},
	{"Extension on secondary opcode 0xc9","Extension on secondary opcode 0xd0","Extension on secondary opcode 0xe0","Extension on secondary opcode 0xe1","Extension on secondary opcode 0xe4","Extension on secondary opcode 0xe5",
	"Extension on secondary opcode 0xe8","Extension on secondary opcode 0xe9","Extension on secondary opcode 0xea","Extension on secondary opcode 0xeb","Extension on secondary opcode 0xec",
	"Extension on secondary opcode 0xed","Extension on secondary opcode 0xee"},
	{"Extension on secondary opcode 0xe9"},	
	{"Extension on secondary opcode 0xe0"},	
	{"Extension on secondary opcode 0xe1","Extension on secondary opcode 0xe9"},
	{"Extension on secondary opcode 0xc1","Extension on secondary opcode 0xc9","Extension on secondary opcode 0xd9","Extension on secondary opcode 0xe1","Extension on secondary opcode 0xe9","Extension on secondary opcode 0xf1",
	"Extension on secondary opcode 0xf9"},
	{"Extension on secondary opcode 0xe0"}},
	
	{{"Extension on secondary opcode 0xf0","Extension on secondary opcode 0xf1","Extension on secondary opcode 0xf2","Extension on secondary opcode 0xf3","Extension on secondary opcode 0xf4","Extension on secondary opcode 0xf5",
	"Extension on secondary opcode 0xf6","Extension on secondary opcode 0xf7","Extension on secondary opcode 0xf8","Extension on secondary opcode 0xf9","Extension on secondary opcode 0xfa",
	"Extension on secondary opcode 0xfb","Extension on secondary opcode 0xfc",
	"Extension on secondary opcode 0xfd","Extension on secondary opcode 0xfe","Extension on secondary opcode 0xff"},	
	{"Extension on secondary opcode 0xe0","Extension on secondary opcode 0xe0","Extension on secondary opcode 0xe1","Extension on secondary opcode 0xe1","Extension on secondary opcode 0xe1","Extension on secondary opcode 0xe2",
	"Extension on secondary opcode 0xe2","Extension on secondary opcode 0xe3","Extension on secondary opcode 0xe3","Extension on secondary opcode 0xe4","Extension on secondary opcode 0xe4",
	"Extension on secondary opcode 0xe4"},	
	{"Extension on secondary opcode 0xe0"}}};
	
	public static final boolean[][][] secondary__one_byte_has_extension=new boolean[][][] 
			
	{{{false,false},{false,false},{true,true},{true,true,true,true,true,true,true,true,true,true,true,true,true},
	  {true},{true},{true,true},{true,true,true,true,true,true,true},
	  {true}},
	 {{true,true,true,true,true,true,true,true,true,true,true,true,true,true,true,true},
	  {true,false,true,false,false,true,false,true,false,true,false,false},{true}
	  
	 }};
	 
	public static final boolean[][][] secondary_one_byte_has_register=new boolean[][][]
		{{{true,true},{true,true},{false,false},{false,false,false,false,false,false,false,false,false,false,false,false,false},
		  {false},{false},{false,false},{false,false,false,false,false,false,false},{false}},
		 {{false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false},
		  {false,false,false,false,false,false,false,false,false,false,false,false},{false}
		  
	}};
	public static final int[][] secondary_prim_indexes = new int[][] {{0xD4,0xD5,0xD8,0xD9,0xDA,0xDB,0xDD,0xDE,0xDF},{0xD9,0xDB,0xDF}};
	
	public static final int[] secondary_prefix_indexes = new int[] {0x00,0x9B};
	
		
	
	
	public static final int[][][] extension_indexes_one_byte_with_two_byte_and_prefixes = new int[][][] {{{0xD1,0xD9},
		{0xC9,0xD0,0xE0,0xE1,0xE4,0xE5,0xE8,0xE9,0xEA,0xEB,0xEC,0xED,0xEE,0xF0,0xF1,0xF2,0xF3,0xF4,0xF5,0xF6,0xF7},{0xE9},
		{0xE0,0xE1,0xE2,0xE3,0xE4},{0xE1,0xE9},{0xC1,0xC9,0xD9,0xE1,0xE9,0xF1,0xF9},{0xE0}},{{0xE2,0xE3},{0xE0}}};
	
	public static final int[][][] extensions_one_byte_with_sec_prefix = {{{0x01,0x02,0x04,0x05,0x06,0x07},{0x05},{0x04},{0x04,0x05},
		{0x00,0x01,0x03,0x04,0x05,0x06,0x07},{0x04}},{{0x04},{0x04}}};
		
		
	public static final int[][] primary_opcodes_for_ext_with_sec_prefix ={{0xD8,0xD9,0xDA,0xDB,0xDD,0xDE,0xDF},{0xDB,0xDE}};
	
	public static final int[][] extension_indexes_one_byte_with_prefixes = new int[][] {{0x80,0x81,0x82,0x83,0x8f,0xc0,0xc1,0xc6,0xc7,
			0xd0,0xd1,0xd2,0xd3,0xd8,0xd9,0xda,0xdb,0xdc,0xdd,0xde,0xdf,0xf6,0xf7,0xfe,0xff},{0xD9,0xDB,0xDD,0xDF}};
			
	public static final int[] prefix_to_opcodes_with_extensions_indexes_one_byte = new int[] {0x00,0x9B};
	
	public static final int[] extension_indexes_one_byte_with_prefix_indexes_one_byte_length = new int[] {25,4};

	public static final String[] two_byte_opcodes=new String[] {};
	
	public static final String[] opcode_with_prefix = new String[] {};
	
	public static final String[] opcode_with_ext_and_prefix = new String[] {};
	
	public static final boolean[] is_prefix_one_byte = new boolean[] 
			
			{
				true,false,false,false,false,false,false,false,false,
				false,false,false,false,false,false,false,false,
				false,false,false,false,false,false,false,false,
				false,false,false,false,false,false,false,false,
				false,false,false,false,false,true,false,false,
				false,false,false,false,false,true,false,false,
				false,false,false,false,false,true,false,false,
				false,false,false,false,false,true,false,true,
				true,true,true,true,true,true,true,true,
				true,true,true,true,true,true,true,false,
				false,false,false,false,false,false,false,false,
				false,false,false,false,false,false,false,false,
				false,false,false,true,true,true,true,false,
				false,false,false,false,false,false,false,false,
				false,false,false,false,false,false,false,false,
				false,false,false,false,false,false,false,false,
				false,false,false,false,false,false,false,false,
				false,false,false,false,false,false,false,false,
				false,false,false,false,false,false,false,false,
				false,false,true,false,false,false,false,false,
				false,false,false,false,false,false,false,false,
				false,false,false,false,false,false,false,false,
				false,false,false,false,false,false,false,false,
				false,false,false,false,false,false,false,false,
				false,false,false,false,false,false,false,false,
				false,false,false,false,false,false,false,false,
				false,false,false,false,false,false,false,false,
				false,false,false,false,false,false,false,false,
				false,false,false,false,false,false,false,false,
				false,false,false,false,false,false,false,true,
				false,true,true,false,false,false,false,false,
				false,false,false,false,false,false,false
				
			};
	
	public static final boolean[][] has_register_one_byte = new boolean[][] 
			
		{
			{true,true,true,true,false,false,false,false,true,true,true,
			true,false,false,false,false,true,true,true,true,false,
			false,false,false,true,true,true,true,false,false,false,
			false,true,true,true,true,false,false,false,false,true,
			true,true,true,false,false,false,false,true,true,true,
			true,false,false,false,false,true,true,true,true,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,true,true,false,
			false,false,false,false,true,false,true,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,true,true,true,true,true,true,true,true,true,
			true,true,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,true,true,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false},
			
			{
			false,false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false},
			{
			false,false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false}};
	
	public static final boolean[][] has_extension_one_byte = new boolean[][] {
		{
			false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,true,true,true,true,
			false,false,false,false,false,false,false,false,false,false,false,true,false,false,
			false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,true,true,false,false,false,false,
			true,true,false,false,false,false,false,false,false,false,true,true,true,true,false,false,false,false,
			true,true,true,true,true,true,true,true,false,false,false,false,false,false,false,false,false,false,false,
			false,false,false,false,false,false,false,false,false,false,false,true,true,false,false,false,false,false,false,true,true
			
		},
		{
			true,true,true,true
			
		},
		{
			false
		}
		
	};
	public static final int has_extension_one_byte_length=0;
	
	
	
	public static final String[][] registers = new String[][] {{"AL", "CL","DL", "BL", "AH", "CH", "DH", "BH"},{"AX","CX","DX","BX", "SP", "BP", "SI", "DI"},
		{"EAX","ECX","EDX","EBX", "ESP", "EBP", "ESI", "EDI"},{"MM0","MM1","MM2","MM3", "MM4", "MM5", "MM6", "MM7"}, {"XMM0","XMM1","XMM2","XMM3", "XMM4", "XMM5", "XMM6", "XMM7"},
		{"ES","CS","SS","DS","FS", "GS","res.","res."},{"CR0","invalid","CR2","CR3","CR4", "invalid","invalid","invalid"},{"DR0","DR1","DR2","DR3","DR4", "DR5","DR6.","DR7"}};
	
	public static final String[] r8 = new String[] {"AL", "CL","DL", "BL", "AH", "CH", "DH", "BH"};
	public static final String[] r16 = new String[] {"AX","CX","DX","BX", "SP", "BP", "SI", "DI"};
	public static final String[] r32 = new String[] {"EAX","ECX","EDX","EBX", "ESP", "EBP", "ESI", "EDI"};
	public static final String[] r64 = new String[]{"RAX","RCX","RDX","RBX", "RSP", "RBP", "RSI", "RDI"};
	public static final String[] mm = new String[] {"MM0","MM1","MM2","MM3", "MM4", "MM5", "MM6", "MM7"};
	public static final String[] xmm = new String[]  {"XMM0","XMM1","XMM2","XMM3", "XMM4", "XMM5", "XMM6", "XMM7"};
	public static final String[] sreg = new String[] {"ES","CS","SS","DS","FS", "GS","res.","res."};
	public static final String[] eee_0 = new String[]{"CR0","invalid","CR2","CR3","CR4", "invalid","invalid","invalid"};
	public static final String[] eee_1 = new String[] {"DR0","DR1","DR2","DR3","DR4", "DR5","DR6.","DR7"};
	
	public static final String[][] rex_prefix_bit_1 = {{"R8B","R9B","R10B","R11B","R12B","R13B","R14B","R15B"},{"R8W","R9W","R10W","R11W","R12W","R13W","R14W","R15W"},
			{"R8D","R9D","R10D","R11D","R12D","R13D","R14D","R15D"},{"R8","R9","R10","R11","R12","R13","R14","R15"},{"MM8","MM9","MM10","MM11", "MM12", "MM13", "MM14", "MM15"},
			{"XMM8","XMM9","XMM10","XMM11", "XMM12", "XMM13", "XMM14", "XMM15"},{"ES","CS","SS","DS","FS", "GS","res.","res."},{"CR8","invalid","invalid","invalid","invalid","invalid","invalid","invalid"},
			{"invalid","invalid","invalid","invalid","invalid","invalid","invalid","invalid"}};
	
	public static final String[][] bit64mod = new String[][] {{"[RAX]","[RCX]","[RDX]", "[RBX]", "[sib]","disp32", "[RSI]", "[RDI]"},
		{"[RAX]","[RCX]","[RDX]", "[RBX]", "[sib]",
		"[RBP]", "[RSI]", "[RDI]"}, {"[RAX]","[RCX]","[RDX]", "[RBX]", "[sib]",
			"[RBP]", "[RSI]", "[RDI]"}, {
				
				"AL/AX/EAX/ST0/MM0/XMM0",	
				"CL/CX/ECX/ST1/MM1/XMM1",	
				"DL/DX/EDX/ST2/MM2/XMM2",		
				"BL/BX/EBX/ST3/MM3/XMM3",		
				"AH/SP/ESP/ST4/MM4/XMM4",		
				"CH/BP/EBP/ST5/MM5/XMM5",		
				"DH/SI/ESI/ST6/MM6/XMM6",		
				"BH/DI/EDI/ST7/MM7/XMM7" 
				
			}};
			
	//reg*(Math.pow(2,i))
	public static final String[] bit64SIB_index = new String[] {"[RAX]","[RCX]","[RDX]", "[RBX]", "none",
					"[RBP]", "[RSI]", "[RDI]"};
	
	public static final String[] bit32SIB_index = new String[] {"[EAX]","[ECX]","[EDX]", "[EBX]", "none",
			"[EBP]", "[ESI]", "[EDI]"};
	
	public static final String[] bit64SIB_index_REX = new String[] {"[R8]","[R9]","[R10]", "[R11]", "[R12]",
			"[R13]", "[R14]", "[R15]"};
	
	public static final String[] bit32SIB_index_REX = new String[] {"[R8D]","[R9D]","[R10D]", "[R11D]", "[R12D]",
		"[R13D]", "[R14D]", "[R15D]"};; 

	public static final short[] SIB_scalar = new short[]{0,2,4,8};
	
	public static final String[] bit64SIB_base =new String[]{"[RAX]","[RCX]","[RDX]", "[RBX]", "[RSP]",
			"none", "[RSI]", "[RDI]"};  
	
	public static final String[] bit64SIB_base_REX =new String[]{"[R8]","[R9]","[R10]", "[R11]", "[R12]",
			"none", "[R14]", "[R15]"};
	
	public static final String[] bit32SIB_base =new String[] {"[EAX]","[ECX]","[EDX]", "[EBX]", "[ESP]",
			"none", "[ESI]", "[EDI]"};
	
	public static final String[] bit32SIB_base_REX =new String[]{"[R8D]","[R9D]","[R10D]", "[R11D]", "[R12D]",
			"none", "[R14D]", "[R15D]"};
	
	public static final String[] bit64SIB_disp_exception = new String[] {"","RBP","RBP"};
	
	public static final String[] bit32SIB_disp_exception = new String[] {"","EBP","EBP"};
	
public static final String[] bit64SIB_disp_exception_REX = new String[] {"","R13","R13"};
	
	public static final String[] bit32SIB_disp_exception_REX = new String[] {"","R13D","R13D"};
	
	public static final short[] SIB_disp_excep = new short[]{32,8,32};
		
	public static final String[][] bit32mod = new String[][] {{"[EAX]","[ECX]","[EDX]", "[EBX]", "[sib]","disp32", "[ESI]", "[EDI]"},
		{"[EAX]","[ECX]","[EDX]", "[EBX]", "[sib]",
		"[EBP]", "[ESI]", "[EDI]"}, {"[EAX]","[ECX]","[EDX]", "[EBX]", "[sib]",
			"[EBP]", "[ESI]", "[EDI]"},
			{
				"AL/AX/EAX/ST0/MM0/XMM0",	
				"CL/CX/ECX/ST1/MM1/XMM1",	
				"DL/DX/EDX/ST2/MM2/XMM2",		
				"BL/BX/EBX/ST3/MM3/XMM3",		
				"AH/SP/ESP/ST4/MM4/XMM4",		
				"CH/BP/EBP/ST5/MM5/XMM5",		
				"DH/SI/ESI/ST6/MM6/XMM6",		
				"BH/DI/EDI/ST7/MM7/XMM7" 
				
			}};
	
				
	public static final short[] displacement =new short[]{0,8,32,0};
	
	public static final short[] displacement16 = new short[]{0,8,16,0};
	
	public static final String[] bit64mod_00 = new String[] {"[RAX]","[RCX]","[RDX]", "[RBX]", "[sib]",
			"[RIP]", "[RSI]", "[RDI]"}; 
	
	public static final short[] disp__at_loc_00_32_64 = new short[]{0,0,0,0,0,32,0,0};
	
	public static final String[] bit64mod_01 = new String[] {"[RAX]","[RCX]","[RDX]", "[RBX]", "[sib]",
			"[RBP]", "[RSI]", "[RDI]"}; //Disp8

	public static final String[] bit64mod_10 = new String[] {"[RAX]","[RCX]","[RDX]", "[RBX]", "[sib]",
			"[RBP]", "[RSI]", "[RDI]"}; //Disp32
	
	
	public static final String[] bit32mod_00 = new String[] {"[EAX]","[ECX]","[EDX]", "[EBX]", "[sib]",
			"[EIP]", "[ESI]", "[EDI]"}; 
	public static final String[] bit32mod_01 = new String[] {"[EAX]","[ECX]","[EDX]", "[EBX]", "[sib]",
			"[EBP]", "[ESI]", "[EDI]"}; //Disp8

	public static final String[] bit32mod_10 = new String[] {"[EAX]","[ECX]","[EDX]", "[EBX]", "[sib]",
			"[EBP]", "[ESI]", "[EDI]"}; //Disp32
	
	public static final String[][] bit32_and_64mod_11 = new String[][] {{"AL","AX","EAX","RAX","ST0","MM0","XMM0"},{"CL","CX","ECX","RCX","ST1","MM1","XMM1"},
		{"DL","DX","EDX","RDX","ST2","MM2","XMM2"},{"BL","BX","EBX","RBX","ST3","MM3","XMM3"},{"AH","SP","ESP","RSP","ST4","MM4","XMM4"},{"CH","BP","EBP","RBP","ST5","MM5","XMM5"},
		{"DH","SI","ESI","RSI","ST6","MM6","XMM6"},{"BH","DI","EDI","RDI","ST7","MM7","XMM7"}};
		
	public static final String[] bit64mod_00_REX = new String[] {"[R8]","[R9]","[R10]", "[R11]", "[sib]",
				"[RIP]", "[R14]", "[R15]"}; 
	public static final String[] bit64mod_01_REX = new String[] {"[R8]","[R9]","[R10]", "[R11]", "[sib]",
				"[R13]", "[R14]", "[R15]"}; //Disp8

	public static final String[] bit64mod_10_REX = new String[] {"[R8]","[R9]","[R10]", "[R11]", "[sib]",
				"[R13]", "[R14]", "[R15]"}; //Disp32
	
	public static final String[] bit32mod_00_REX = new String[] {"[R8D]","[R9D]","[R10D]", "[R11D]", "[sib]",
			"[EIP]", "[R14D]", "[R15D]"}; 
	public static final String[] bit32mod_01_REX = new String[] {"[R8D]","[9]","[R10D]", "[R11D]", "[sib]",
			"[R13D]", "[R14D]", "[R15D]"}; //Disp8

	public static final String[] bit32mod_10_REX = new String[] {"[R8D]","[R9D]","[R10D]", "[R11D]", "[sib]",
			"[R13D]", "[R14D]", "[R15D]"}; //Disp32
	
	public static final String[][] bit32_and_64mod_11_REX = new String[][] {{"R8B","R8W","R8D","R8","ST0","MM0","XMM8"},{"R9B","R9W","R9D","R9","ST1","MM1","XMM9"},
		{"R10B","R10W","R10D","R10","ST2","MM2","XMM10"},{"R11B","R11W","R11D","R11","ST3","MM3","XMM11"},{"R12B","R12W","R12D","R12","ST4","MM4","XMM12"},{"R13B","R13W","R13D","R13","ST5","MM5","XMM13"},
		{"R14B","R14W","R14D","R14","ST6","MM6","XMM14"},{"R15B","R15W","R15D","R15","ST7","MM7","XMM15"}};
	
	public static final String[] bit16mod_00 = new String[] {"[BX+SI]","[BX+DI]","[BP+SI]", "[BP+DI]", "[SI]",
			"[DI]","", "[BX]"}; 
	
	public static final short[] disp__at_loc_00_16 = new short[]{0,0,0,0,0,16,0,0};
	
	public static final String[] bit16mod_01 = new String[] {"[BX+SI}","[BX+DI]","[BP+SI]", "[BP+DI]", "[SI]",
			"[DI]", "[BP]", "[BX]"}; //disp8

	public static final String[] bit16mod_10 = new String[] {"[BX+SI}","[BX+DI]","[BP+SI]", "[BP+DI]", "[SI]",
			"[DI]", "[BP]", "[BX]" }; //disp16
	
	public static final String[][] bit16mod_11 =  new String[][] {{"AL","AX","EAX","ST0","MM0","XMM0"},{"CL","CX","ECX","ST1","MM1","XMM1"},
		{"DL","DX","EDX","ST2","MM2","XMM2"},{"BL","BX","EBX","ST3","MM3","XMM3"},{"AH","SP","ESP","ST4","MM4","XMM4"},{"CH","BP","EBP","ST5","MM5","XMM5"},
		{"DH","SI","ESI","ST6","MM6","XMM6"},{"BH","DI","EDI","ST7","MM7","XMM7"}};
	
	
	public static final int[] mod_values = new int[] {0,1,2,3};

	
	public int getLengthOf(short op,byte[] next,short ind);
}


